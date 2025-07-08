package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.details.GameDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDetailsRepository extends MongoRepository<GameDetails,Long> {

}
