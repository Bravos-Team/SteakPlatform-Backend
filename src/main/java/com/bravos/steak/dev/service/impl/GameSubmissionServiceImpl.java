package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.GameS3Config;
import com.bravos.steak.common.security.JwtTokenClaims;
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
import com.bravos.steak.dev.service.LogDevService;
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
    private final LogDevService logDevService;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final AwsS3Service awsS3Service;
    private final GameS3Config gameS3Config;

    @Autowired
    public GameSubmissionServiceImpl(SnowflakeGenerator snowflakeGenerator, GameSubmissionRepository gameSubmissionRepository,
                                     LogDevService logDevService, MongoTemplate mongoTemplate, ObjectMapper objectMapper,
                                     AwsS3Service awsS3Service, GameS3Config gameS3Config) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameSubmissionRepository = gameSubmissionRepository;
        this.logDevService = logDevService;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.awsS3Service = awsS3Service;
        this.gameS3Config = gameS3Config;
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

        logDevService.saveLog(createrid,publisherId,"đã tạo project với tên {}",projectName);

        return gameSubmission.getId();

    }

    @Override
    public void saveDraftProject(SaveProjectRequest saveProjectRequest) {

        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

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
            update.set("status", GameSubmissionStatus.DRAFT);

            try {
                Query query = Query.query(Criteria.where("_id").is(projectId));
                mongoTemplate.updateFirst(query,update, GameSubmission.class);
                logDevService.saveLog(jwtTokenClaims.getId(),publisherIdAndStatus.publisherId,
                        "Project {} has been updated by {}",projectId,jwtTokenClaims.getId());
            } catch (Exception e) {
                log.error("Error when saving project: {}",e.getMessage(),e);
                throw new RuntimeException("Error when saving project");
            }
        }
    }

    @Override
    public void updateBuild(UpdatePreBuildRequest updatePreBuildRequest) {

        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();
        long projectId = updatePreBuildRequest.getProjectId();
        var publisherIdAndStatus = checkProjectOwnership(projectId);

        GameSubmission gameSubmission = gameSubmissionRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("Project not found"));

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

        try {
            gameSubmissionRepository.save(gameSubmission);
            logDevService.saveLog(jwtTokenClaims.getId(), publisherIdAndStatus.publisherId,
                    "Project {} has been updated build by {}", projectId, jwtTokenClaims.getId());
        } catch (Exception e) {
            log.error("Error when updating build: {}", e.getMessage(), e);
            throw new RuntimeException("Error when updating build");
        }

        Thread.startVirtualThread(() -> {
            if(oldDownloadUrl != null && !oldDownloadUrl.isBlank()) {
                String objectKey = oldDownloadUrl.replace(gameS3Config.getBaseUrl(), "");
                awsS3Service.deleteObject(gameS3Config.getBucketName(),objectKey);
            }
        });

    }

    @Override
    public void submitGameSubmission(Long projectId) {

        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        var publisherIdAndStatus = checkProjectOwnership(projectId);

        GameSubmission gameSubmission = gameSubmissionRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("Project not found"));

        if(gameSubmission.getStatus() != GameSubmissionStatus.DRAFT) {
            throw new BadRequestException("You can only publish draft project");
        }

        StringBuilder errorMessage = getErrorMessage(gameSubmission);

        if(!errorMessage.isEmpty()) {
            errorMessage.insert(0, "Cannot publish project: \n");
            throw new BadRequestException("Cannot publish project: " + errorMessage);
        }

        gameSubmission.setStatus(GameSubmissionStatus.PENDING_REVIEW);
        gameSubmission.setUpdatedAt(DateTimeHelper.currentTimeMillis());

        try {
            gameSubmissionRepository.save(gameSubmission);
            logDevService.saveLog(jwtTokenClaims.getId(), publisherIdAndStatus.publisherId,
                    "Project {} has been requested by {}", projectId, jwtTokenClaims.getId());
        } catch (Exception e) {
            log.error("Error when publishing project: {}", e.getMessage(), e);
            throw new RuntimeException("Error when publishing project");
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

        if(gameSubmission.getBuildInfo() == null) {
            errorMessage.append("You need to submit project build \n");
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
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();
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
