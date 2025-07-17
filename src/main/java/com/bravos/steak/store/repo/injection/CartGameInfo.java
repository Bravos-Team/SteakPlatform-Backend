package com.bravos.steak.store.repo.injection;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.bravos.steak.store.entity.details.GameDetails}
 */
@Value
public class CartGameInfo implements Serializable {
    Long id;
    String title;
    String thumbnail;
}