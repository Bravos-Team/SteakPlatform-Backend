package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.repo.injection.GameIdTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    List<UserGame> findByUserId(Long userId);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GameIdTitle(ug.game.id, ug.game.name) " +
           "FROM UserGame ug WHERE ug.user.id = :userId")
    List<GameIdTitle> findGameIdTitleByUserId(Long userId);

}