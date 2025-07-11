package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.details.GameDetails;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GameDetailsRepository extends MongoRepository<GameDetails,Long> {

    @Query(value = "{ 'id' : {$in: ?0} }", fields = "{ 'id' : 1, 'title' : 1, 'thumbnail' : 1}")
    List<GameDetails> findForLibraryByIdIn(List<Long> gameIds);

    List<CartGameInfo> findByIdIn(Collection<Long> ids);

}
