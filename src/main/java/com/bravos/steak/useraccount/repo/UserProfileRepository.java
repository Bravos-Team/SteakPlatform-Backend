package com.bravos.steak.useraccount.repo;

import com.bravos.steak.useraccount.entity.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile,Long> {
}
