package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetails {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Order.class)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Game.class)
    private Game game;

    private BigDecimal price;

}
