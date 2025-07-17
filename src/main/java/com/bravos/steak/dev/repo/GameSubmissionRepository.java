package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.repo.custom.CustomGameSubmissionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSubmissionRepository extends MongoRepository<GameSubmission,Long>, CustomGameSubmissionRepository {
    GameSubmission findByNameAndPublisherId(String name, Long publisherId);
    GameSubmission findByIdAndPublisherId(Long id, Long publisherId);
}
