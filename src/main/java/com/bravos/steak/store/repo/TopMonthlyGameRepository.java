package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Top50MonthlyTrendingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopMonthlyGameRepository extends JpaRepository<Top50MonthlyTrendingRecord, Long> {

}
