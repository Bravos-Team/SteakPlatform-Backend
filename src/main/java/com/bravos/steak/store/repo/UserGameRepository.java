package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.repo.injection.LibraryInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    List<UserGame> findByUserId(Long userId);

    @Query("SELECT new com.bravos.steak.store.repo.injection.LibraryInfo(ug.game.id,ug.ownedAt,ug.playRecentDate) " +
           "FROM UserGame ug WHERE ug.user.id = :userId")
    List<LibraryInfo> findLibraryInfoByUserId(@Param("userId") Long userId, Sort sort);

}