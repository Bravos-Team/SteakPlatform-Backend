package com.bravos.steak.store.repo.injection;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
                        @ColumnResult(name = "gameId", type = Long.class),
                        @ColumnResult(name = "peakConcurrent", type = Long.class),
                        @ColumnResult(name = "avgConcurrent", type = Double.class),
                        @ColumnResult(name = "growthRate", type = Double.class),
                        @ColumnResult(name = "trendingScore", type = Double.class)
                }
        )
)
public class TrendingStatistic {

    Long gameId;

    Long peakConcurrent;

    Double avgConcurrent;

    Double growthRate;

    Double trendingScore;

}
