package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Top50MonthlyTrendingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopMonthlyGameRepository extends JpaRepository<Top50MonthlyTrendingRecord, Long> {

    @Query("SELECT COUNT(*) > 0 FROM Top50MonthlyTrendingRecord WHERE id.month = ?1 AND id.year = ?2")
    boolean existsByTime(int currentMonth, int currentYear);

}
