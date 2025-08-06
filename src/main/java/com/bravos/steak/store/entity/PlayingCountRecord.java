package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
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
@Table(name = "playing_count_record")
public class PlayingCountRecord {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Game.class)
    Game game;

    @Builder.Default
    Long count = 0L;

    @Builder.Default
    Long recordAt = DateTimeHelper.currentTimeMillis();



}
