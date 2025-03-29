package com.bravos.steak.account.repo;

import com.bravos.steak.account.entity.AccountProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<AccountProfile,Long> {
}
