package com.bravos.steak.dev.repo.custom.impl;

import com.bravos.steak.common.repo.CustomMongoRepository;
import com.bravos.steak.dev.repo.custom.CustomGameSubmissionRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomGameSubmissionRepositoryImpl extends CustomMongoRepository implements CustomGameSubmissionRepository {

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
        return this.getProjectionByQuery(query, GameSubmissionProjection.class).publisherId;
    }

    private static class GameSubmissionProjection {
        public Long publisherId;
    }

}
