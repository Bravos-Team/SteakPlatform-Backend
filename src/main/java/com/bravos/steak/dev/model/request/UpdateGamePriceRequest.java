package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class UpdateGamePriceRequest {

    @NotNull
    Long gameId;

    @DecimalMin(value = "0.0", message = "Price must be greater than 0")
    Double price;

}
