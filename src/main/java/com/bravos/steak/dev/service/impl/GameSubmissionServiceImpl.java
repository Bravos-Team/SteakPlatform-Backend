package com.bravos.steak.dev.service.impl;

import com.bravos.steak.administration.entity.review.From;
import com.bravos.steak.administration.entity.review.ReviewReply;
import com.bravos.steak.administration.repo.ReviewReplyRepository;
import com.bravos.steak.common.model.GameS3Config;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.service.storage.impl.AwsS3Service;
import com.bravos.steak.dev.entity.gamesubmission.BuildInfo;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import com.bravos.steak.dev.model.request.PublisherReviewReplyRequest;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import com.bravos.steak.dev.repo.GameSubmissionRepository;
import com.bravos.steak.dev.repo.custom.CustomGameSubmissionRepository;
import com.bravos.steak.dev.service.GameSubmissionService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.exceptions.ForbiddenException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameSubmissionServiceImpl implements GameSubmissionService {

    private final SnowflakeGenerator snowflakeGenerator;
    private final GameSubmissionRepository gameSubmissionRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final AwsS3Service awsS3Service;
    private final GameS3Config gameS3Config;
    private final SessionService sessionService;
    private final ReviewReplyRepository reviewReplyRepository;

    @Autowired
    public GameSubmissionServiceImpl(SnowflakeGenerator snowflakeGenerator, GameSubmissionRepository gameSubmissionRepository,
                                     MongoTemplate mongoTemplate, ObjectMapper objectMapper, AwsS3Service awsS3Service,
                                     GameS3Config gameS3Config, SessionService sessionService,
                                     ReviewReplyRepository reviewReplyRepository) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameSubmissionRepository = gameSubmissionRepository;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.awsS3Service = awsS3Service;
        this.gameS3Config = gameS3Config;
        this.sessionService = sessionService;
        this.reviewReplyRepository = reviewReplyRepository;
    }

    @Override
    public Long createProject(String projectName) {

        if(projectName == null || projectName.isBlank()) {
            throw new BadRequestException("Project name cannot be blank");
        }

        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        long createrid = jwtTokenClaims.getId();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        long submissionId = snowflakeGenerator.generateId();

        if(gameSubmissionRepository.findByNameAndPublisherId(projectName,publisherId) != null) {
            throw new ConflictDataException("Project name is existed in your submissions");
        }

        GameSubmission gameSubmission = new GameSubmission(submissionId,publisherId,createrid,projectName);
        gameSubmission.setStatus(GameSubmissionStatus.DRAFT);
        gameSubmission.setUpdatedAt(DateTimeHelper.currentTimeMillis());

        try {
            gameSubmission = gameSubmissionRepository.save(gameSubmission);
        } catch (Exception e) {
            log.error("Error when creating a project: {}",e.getMessage(),e);
            throw new RuntimeException("Error when creating a project");
        }
        return gameSubmission.getId();

    }

    @Override
    public void saveDraftProject(SaveProjectRequest saveProjectRequest) {
        long projectId = saveProjectRequest.getId();
        var publisherIdAndStatus = checkProjectOwnership(projectId);

        if(publisherIdAndStatus.status != GameSubmissionStatus.DRAFT &&
                publisherIdAndStatus.status != GameSubmissionStatus.PENDING_REVIEW) {
            throw new BadRequestException("You can only update draft or pending review project");
        }

        Map<String,Object> changedData = objectMapper.convertValue(
                saveProjectRequest,
                new TypeReference<>() {});
        changedData.remove("id");

        if(!changedData.isEmpty()) {

            Update update = new Update();

            for(var x : changedData.entrySet()) {
                Object value = x.getValue();
                if (value != null) {
                    update.set(x.getKey(),value);
                }
            }

            update.set("updatedAt",DateTimeHelper.currentTimeMillis());

            try {
                Query query = Query.query(Criteria.where("_id")
                        .is(projectId)
                        .and("publisherId").is(publisherIdAndStatus.publisherId));
                mongoTemplate.updateFirst(query,update, GameSubmission.class);
            } catch (Exception e) {
                log.error("Error when saving project: {}",e.getMessage(),e);
                throw new RuntimeException("Error when saving project");
            }
        }
    }

    @Override
    public void updateBuildProject(UpdatePreBuildRequest updatePreBuildRequest) {

        long projectId = updatePreBuildRequest.getProjectId();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        GameSubmission gameSubmission = gameSubmissionRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("Project not found"));

        if(!gameSubmission.getPublisherId().equals(publisherId)) {
            throw new ForbiddenException("You are not the owner of this project");
        }

        if(gameSubmission.getStatus() != GameSubmissionStatus.DRAFT &&
                gameSubmission.getStatus() != GameSubmissionStatus.PENDING_REVIEW) {
            throw new BadRequestException("You can only update build for draft or pending review project");
        }

        String oldDownloadUrl = gameSubmission.getBuildInfo() != null ?
                gameSubmission.getBuildInfo().getDownloadUrl() : null;

        BuildInfo buildInfo = new BuildInfo();
        buildInfo.setVersionName(updatePreBuildRequest.getVersionName());
        buildInfo.setExecPath(updatePreBuildRequest.getExecPath());
        buildInfo.setDownloadUrl(updatePreBuildRequest.getDownloadUrl());
        buildInfo.setChecksum(updatePreBuildRequest.getChecksum());

        gameSubmission.setBuildInfo(buildInfo);
        gameSubmission.setUpdatedAt(DateTimeHelper.currentTimeMillis());

        Update update = new Update();
        update.set("buildInfo", buildInfo);
        update.set("updatedAt", DateTimeHelper.currentTimeMillis());

        try {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(projectId)),
                    update,
                    GameSubmission.class
            );
        } catch (Exception e) {
            log.error("Error when updating build: {}", e.getMessage(), e);
            throw new RuntimeException("Error when updating build");
        }

        if(oldDownloadUrl != null && !oldDownloadUrl.isBlank()) {
            awsS3Service.deleteObject(gameS3Config.getBucketName(),awsS3Service.getObjectNameFromUrl(oldDownloadUrl));
        }

    }

    @Override
    public void submitGameSubmission(Long projectId) {

        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        GameSubmission gameSubmission = gameSubmissionRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("Project not found"));

        if(!gameSubmission.getPublisherId().equals(publisherId)) {
            throw new ForbiddenException("You are not the owner of this project");
        }

        if(gameSubmission.getStatus() != GameSubmissionStatus.DRAFT &&
                gameSubmission.getStatus() != GameSubmissionStatus.PENDING_REVIEW) {
            throw new BadRequestException("Project must be in draft or pending review status to submit");
        }

        StringBuilder errorMessage = getErrorMessage(gameSubmission);

        if(!errorMessage.isEmpty()) {
            errorMessage.insert(0, "Cannot publish project: \n");
            throw new BadRequestException("Cannot publish project: " + errorMessage);
        }

        Update update = new Update();
        update.set("status", GameSubmissionStatus.PENDING_REVIEW);
        update.set("updatedAt", DateTimeHelper.currentTimeMillis());

        try {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(projectId)),
                    update,
                    GameSubmission.class
            );
        } catch (Exception e) {
            log.error("Error when updating project status: {}", e.getMessage(), e);
            throw new RuntimeException("Error when updating project status");
        }

    }

    @Override
    public GameSubmission detailByIdAndPublisher(Long submissionId, Long publisherId) {
        GameSubmission gameSubmission = gameSubmissionRepository.findByIdAndPublisherId(submissionId, publisherId);
        if(gameSubmission == null) {
            throw new BadRequestException("Project not found or you are not the owner of this project");
        }
        return gameSubmissionRepository.findByIdAndPublisherId(submissionId,publisherId);
    }

    private StringBuilder getErrorMessage(GameSubmission gameSubmission) {
        StringBuilder errorMessage = new StringBuilder();

        if(gameSubmission.getName().isBlank()) {
            errorMessage.append("Project name cannot be blank. \n");
        }

        BuildInfo buildInfo = gameSubmission.getBuildInfo();

        if(buildInfo == null) {
            errorMessage.append("You need to submit project build \n");
        } else {
            if(buildInfo.getVersionName() == null || buildInfo.getVersionName().isBlank()) {
                errorMessage.append("Project version name cannot be blank. \n");
            }

            if(buildInfo.getExecPath() == null || buildInfo.getExecPath().isBlank()) {
                errorMessage.append("Project executable path cannot be blank. \n");
            }

            if(buildInfo.getDownloadUrl() == null || buildInfo.getDownloadUrl().isBlank()) {
                errorMessage.append("Project download URL cannot be blank. \n");
            }

            if(buildInfo.getChecksum() == null || buildInfo.getChecksum().isBlank()) {
                errorMessage.append("Project checksum cannot be blank. \n");
            }
        }

        if(gameSubmission.getThumbnail() == null || gameSubmission.getThumbnail().isBlank()) {
            errorMessage.append("Project thumbnail cannot be blank. \n");
        }

        if(gameSubmission.getLongDescription() == null || gameSubmission.getLongDescription().isBlank()) {
            errorMessage.append("Project description cannot be blank. \n");
        }

        if(gameSubmission.getShortDescription() == null || gameSubmission.getShortDescription().isBlank()) {
            errorMessage.append("Project short description cannot be blank. \n");
        }

        if(gameSubmission.getPrice() == null || gameSubmission.getPrice() < 0) {
            errorMessage.append("Project price cannot be negative. \n");
        }

        if(gameSubmission.getMedia().length < 1) {
            errorMessage.append("You must upload at least one media file. \n");
        }

        if(gameSubmission.getSystemRequirements() == null) {
            errorMessage.append("You must provide system requirements for your project. \n");
        }
        return errorMessage;
    }

    @Override
    public Page<GameSubmissionListItem> getProjectListByPublisher(String status, String keyword, int page, int size) {
        GameSubmissionStatus submissionStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                submissionStatus = GameSubmissionStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid submission status: " + status);
            }
        }
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        try {
            if(keyword != null && keyword.startsWith("id:")) {
                long projectId;
                try {
                    projectId = Long.parseLong(keyword.substring(3));
                } catch (BadRequestException e) {
                    throw new BadRequestException("Invalid project ID format");
                }
                GameSubmissionListItem submission = gameSubmissionRepository.getGameSubmissionListById(projectId, publisherId);
                if(submission == null) {
                    return new PageImpl<>(List.of(), Page.empty().getPageable(), 0);
                }
                return new PageImpl<>(List.of(submission), Page.empty().getPageable(), 1);
            }
            return gameSubmissionRepository.getGameSubmissionListDisplay(
                    publisherId, submissionStatus, keyword, page, size, Sort.by(
                            Sort.Direction.DESC, "updatedAt"
                    ));
        } catch (Exception e) {
            log.error("Error when fetching project list: {}", e.getMessage(), e);
            throw new RuntimeException("Error when fetching project list");
        }
    }

    @Override
    public void reSubmitGameSubmission(PublisherReviewReplyRequest publisherReviewReplyRequest) {
        JwtTokenClaims tokenClaims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        var publisherIdAndStatus = checkProjectOwnership(publisherReviewReplyRequest.getSubmissionId());
        if(publisherIdAndStatus.status != GameSubmissionStatus.NEED_UPDATE) {
            throw new BadRequestException("You can only re-submit project that is in need update status");
        }
        ReviewReply reviewReply = ReviewReply.builder()
                .id(snowflakeGenerator.generateId())
                .gameSubmissionId(publisherReviewReplyRequest.getSubmissionId())
                .attachments(publisherReviewReplyRequest.getAttachments())
                .content(publisherReviewReplyRequest.getContent())
                .from(new From("publisher",tokenClaims.getId()))
                .repliedAt(DateTimeHelper.currentTimeMillis())
                .build();
        try {
            reviewReplyRepository.save(reviewReply);
        } catch (Exception e) {
            log.error("Failed to save review reply: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save review reply: " + e.getMessage());
        }

        try {
            this.submitGameSubmission(publisherReviewReplyRequest.getSubmissionId());
        } catch (Exception e) {
            reviewReplyRepository.deleteById(reviewReply.getId());
            log.error("Failed to re-submit game submission: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void updateGameSubmissionStatus(Long submissionId, GameSubmissionStatus status) {
        Update update = new Update();
        update.set("status", status);
        update.set("updatedAt", DateTimeHelper.currentTimeMillis());
        try {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(submissionId)),
                    update,
                    GameSubmission.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game submission status: " + e.getMessage());
        }
    }

    private CustomGameSubmissionRepository.PublisherIdAndStatus checkProjectOwnership(long projectId) {
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        Long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        CustomGameSubmissionRepository.PublisherIdAndStatus publisherIdAndStatus;

        try {
            publisherIdAndStatus = gameSubmissionRepository.getPublisherIdAndStatusById(projectId);
        } catch (Exception e) {
            log.error("Error when verifying project: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot verify your project");
        }

        if(publisherIdAndStatus == null) {
            throw new BadRequestException("Project not found");
        }

        if(!publisherId.equals(publisherIdAndStatus.publisherId)) {
            throw new ForbiddenException("You are not owner of this project");
        }

        return publisherIdAndStatus;
    }

}
