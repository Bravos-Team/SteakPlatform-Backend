package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.gamesubmission.BuildInfo;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import com.bravos.steak.dev.repo.GameSubmissionRepository;
import com.bravos.steak.dev.service.LogDevService;
import com.bravos.steak.dev.service.PublisherPublishGameService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.exceptions.ForbiddenException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class PublisherPublishGameServiceImpl implements PublisherPublishGameService {

    private final SnowflakeGenerator snowflakeGenerator;
    private final GameSubmissionRepository gameSubmissionRepository;
    private final LogDevService logDevService;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public PublisherPublishGameServiceImpl(SnowflakeGenerator snowflakeGenerator, GameSubmissionRepository gameSubmissionRepository,
                                           LogDevService logDevService, MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameSubmissionRepository = gameSubmissionRepository;
        this.logDevService = logDevService;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
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
        gameSubmission.setUpdatedAt(new Date());

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

        long publisherId = checkProjectOwnership(projectId);

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
            update.set("updatedAt",new Date());

            try {
                Query query = Query.query(Criteria.where("_id").is(projectId));
                mongoTemplate.updateFirst(query,update, GameSubmission.class);
                logDevService.saveLog(jwtTokenClaims.getId(),publisherId,
                        "Project {} has been updated by {}",projectId,publisherId);
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
        long publisherId = checkProjectOwnership(projectId);

        GameSubmission gameSubmission = gameSubmissionRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("Project not found"));

        if(gameSubmission.getStatus() != GameSubmissionStatus.DRAFT) {
            throw new BadRequestException("You can only update build for draft project");
        }

        BuildInfo buildInfo = new BuildInfo();
        buildInfo.setVersionName(updatePreBuildRequest.getVersionName());
        buildInfo.setExecPath(updatePreBuildRequest.getExecPath());
        buildInfo.setDownloadUrl(updatePreBuildRequest.getDownloadUrl());
        buildInfo.setChecksum(updatePreBuildRequest.getChecksum());

        gameSubmission.setBuildInfo(buildInfo);
        gameSubmission.setUpdatedAt(new Date());

        try {
            gameSubmissionRepository.save(gameSubmission);
            logDevService.saveLog(jwtTokenClaims.getId(), publisherId,
                    "Project {} has been updated build by {}", projectId, publisherId);
        } catch (Exception e) {
            log.error("Error when updating build: {}", e.getMessage(), e);
            throw new RuntimeException("Error when updating build");
        }
    }

    private long checkProjectOwnership(long projectId) {
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims)
                SecurityContextHolder.getContext().getAuthentication().getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        long realPublisherId;
        try {
            realPublisherId = gameSubmissionRepository.getPublisherIdByProjectId(projectId);
        } catch (Exception e) {
            log.error("Error when verifying project: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot verify your project");
        }

        if(publisherId != realPublisherId) {
            throw new ForbiddenException("You are not owner of this project");
        }

        return realPublisherId;
    }

    private boolean checkUploadSuccess(String uploadUrl) {

        return true;
    }

}
