package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.store.model.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    String name;

    @Column(precision = 13, scale = 2, nullable = false)
    BigDecimal price;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    GameStatus status = GameStatus.CLOSED;

    Long releaseDate;

    @Builder.Default
    Long createdAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    Long updatedAt = DateTimeHelper.currentTimeMillis();

}
