package com.bravos.steak.useraccount.repo.custom.impl;

import com.bravos.steak.common.repo.CustomMongoRepository;
import com.bravos.steak.useraccount.model.response.UserLoginResponse;
import com.bravos.steak.useraccount.repo.custom.CustomUserProfileRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserProfileRepositoryImpl extends CustomMongoRepository implements CustomUserProfileRepository {

    public CustomUserProfileRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public UserLoginResponse findLoginResponseById(Long id) {
        return this.getProjectionById(id, UserLoginResponse.class);
    }

    @Override
    protected String collectionName() {
        return "UserProfile";
    }
}
