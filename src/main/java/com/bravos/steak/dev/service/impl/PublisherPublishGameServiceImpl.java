package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.repo.GameSubmissionRepository;
import com.bravos.steak.dev.service.PublisherPublishGameService;
import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.exceptions.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublisherPublishGameServiceImpl implements PublisherPublishGameService {

    private final SnowflakeGenerator snowflakeGenerator;
    private final GameSubmissionRepository gameSubmissionRepository;

    @Autowired
    public PublisherPublishGameServiceImpl(SnowflakeGenerator snowflakeGenerator, GameSubmissionRepository gameSubmissionRepository) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameSubmissionRepository = gameSubmissionRepository;
    }

    @Override
    public Long createProject(String projectName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthentication) {
            JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
            long createrid = jwtTokenClaims.getId();
            long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
            long submissionId = snowflakeGenerator.generateId();

            if(gameSubmissionRepository.findByNameAndPublisherId(projectName,publisherId) != null) {
                throw new ConflictDataException("Project name is existed");
            }

            GameSubmission gameSubmission = new GameSubmission(submissionId,publisherId,createrid,projectName);
            try {
                gameSubmission = gameSubmissionRepository.save(gameSubmission);
            } catch (Exception e) {
                log.error("Error when creating a project: {}",e.getMessage(),e);
                throw new RuntimeException("Error when creating a project");
            }
            return gameSubmission.getId();
        }
        throw new ForbiddenException("You don't have permission to create games");
    }

}
