package com.bravos.steak.store.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @Range(min = 1000, message = "Amount must be at least 1000")
    @NotNull
    Long amount;

    @Size(min = 2, max = 5)
    @NotBlank
    String locale;

    @Size(min = 1, max = 255, message = "Order info must be between 1 and 255 characters")
    @NotBlank
    String orderInfo;

}
