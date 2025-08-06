package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.PlayingCountRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayingCountRecordRepository extends JpaRepository<PlayingCountRecord, Long> {
}