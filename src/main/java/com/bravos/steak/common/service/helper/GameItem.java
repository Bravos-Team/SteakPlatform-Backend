package com.bravos.steak.common.service.helper;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameItem {

    private String name;
    private String imageUrl;
    private double price;
    private double originalPrice;
    private Integer discountPercentage;

    public boolean hasDiscount() {
        return discountPercentage != null && discountPercentage > 0;
    }

    public double getDiscountedPrice() {
        return hasDiscount() ? price : originalPrice;
    }

}
