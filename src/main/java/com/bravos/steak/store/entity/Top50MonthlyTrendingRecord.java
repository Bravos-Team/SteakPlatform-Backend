package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "top_50_monthly_trending")
public class Top50MonthlyTrendingRecord {

    @EmbeddedId
    private TrendingRecordId id;

    @ManyToOne(targetEntity = Game.class)
    @JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
    private Game game;

    private Long peakConcurrent;

    private Double avgConcurrent;

    private Double growthRate;

    private Double trendingScore;

}
