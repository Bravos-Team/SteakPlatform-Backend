package com.bravos.steak.dev.repo.custom.impl;

import com.bravos.steak.common.repo.CustomMongoRepository;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import com.bravos.steak.dev.repo.custom.CustomGameSubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomGameSubmissionRepositoryImpl
        extends CustomMongoRepository
        implements CustomGameSubmissionRepository {

    public CustomGameSubmissionRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    protected String collectionName() {
        return "GameSubmission";
    }

    @Override
    public Long getPublisherIdByProjectId(Long projectId) {
        Query query = Query.query(Criteria.where("_id").is(projectId));
        query.fields().include("publisherId");
        var response = this.getProjectionByQuery(query, GameSubmissionProjection.class);
        return response != null ? response.publisherId : null;
    }

    @Override
    public Page<GameSubmissionListItem> getGameSubmissionListDisplay(
            Long publisherId,
            GameSubmissionStatus status,
            String keyword,
            int page, int size, Sort sort) {
        Query query =  new Query();
        if(publisherId != null) {
            query.addCriteria(Criteria.where("publisherId").is(publisherId));
        }
        if(status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if(keyword != null && !keyword.isBlank()) {
            query.addCriteria(Criteria.where("name").regex(keyword, "i"));
        }
        if(sort != null) {
            query.with(sort);
        }
        query.fields().include("id", "publisherId", "name", "status", "buildInfo.versionName", "updatedAt");
        return this.getPageProjectionsByQuery(query, GameSubmissionListItem.class, PageRequest.of(page, size));
    }

    @Override
    public GameSubmissionListItem getGameSubmissionListById(Long id, Long publisherId) {
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("publisherId").is(publisherId));
        query.fields().include("id", "publisherId", "name", "status", "updatedAt");
        return this.getProjectionByQuery(query, GameSubmissionListItem.class);
    }

    @Override
    public PublisherIdAndStatus getPublisherIdAndStatusById(Long id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        query.fields().include("publisherId", "status");
        return this.getProjectionByQuery(query, PublisherIdAndStatus.class);
    }

    private static class GameSubmissionProjection {
        public Long publisherId;
    }

}
