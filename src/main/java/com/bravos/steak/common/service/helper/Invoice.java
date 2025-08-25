package com.bravos.steak.common.service.helper;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Invoice {

    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String paymentMethod;
    private Customer customer;
    private List<GameItem> gameItems;
    private double totalAmount;

    public Invoice(String invoiceNumber, LocalDateTime invoiceDate, String paymentMethod,
                   Customer customer, List<GameItem> gameItems) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.paymentMethod = paymentMethod;
        this.customer = customer;
        this.gameItems = gameItems;
        this.totalAmount = calculateTotal();
    }

    private double calculateTotal() {
        return gameItems.stream()
                .mapToDouble(item -> item.hasDiscount() ? item.getDiscountedPrice() : item.getPrice())
                .sum();
    }

}
