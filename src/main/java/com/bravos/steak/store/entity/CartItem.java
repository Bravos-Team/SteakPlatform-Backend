package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_item")
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

    @Override
    public boolean equals(Object o) {
        if (o == null || game == null || game.getId() == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        if (cartItem.game == null || cartItem.game.getId() == null) return false;
        return Objects.equals(game.getId(), cartItem.game.getId());
    }

    @Override
    public int hashCode() {
        return game != null ? Objects.hashCode(game.getId()) : 0;
    }

}
