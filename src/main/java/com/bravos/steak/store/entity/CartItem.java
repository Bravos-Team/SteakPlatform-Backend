package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cart_item")
public class CartItem {

    @Id
    private Long id;

    @ManyToOne(targetEntity = Game.class)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(targetEntity = Cart.class)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Builder.Default
    private Long addedAt = DateTimeHelper.currentTimeMillis();

}
