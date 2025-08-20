package com.bravos.steak.store.repo.injection;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@SqlResultSetMapping(
        name = "TrendingStatisticMapping",
        classes = @ConstructorResult(
                targetClass = TrendingStatistic.class,
                columns = {
                        @ColumnResult(name = "game_id", type = Long.class),
                        @ColumnResult(name = "peak_concurrent", type = Long.class),
                        @ColumnResult(name = "avg_concurrent", type = BigDecimal.class),
                        @ColumnResult(name = "growth_rate", type = BigDecimal.class),
                        @ColumnResult(name = "trending_score", type = BigDecimal.class)
                }
        )
)
public class TrendingStatistic {

    Long gameId;

    Long peakConcurrent;

    BigDecimal avgConcurrent;

    BigDecimal growthRate;

    BigDecimal trendingScore;

}
