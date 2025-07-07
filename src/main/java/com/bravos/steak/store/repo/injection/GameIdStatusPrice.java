package com.bravos.steak.store.repo.injection;

import com.bravos.steak.store.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameIdStatusPrice {
    private Long id;
    private GameStatus status;
    private BigDecimal price;
}
