package com.bravos.steak.store.model.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Setter
@FieldDefaults(level = PRIVATE)
public class CartResponse {

    public CartResponse(List<CartListItem> items) {
        this.items = items;
        this.totalPrice = calcTotalPrice();
    }

    @Getter
    final List<CartListItem> items;

    final Double totalPrice;

    private Double calcTotalPrice() {
        if(items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(CartListItem::getPrice)
                .sum();
    }

}
