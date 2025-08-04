package com.bravos.steak.common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CustomPageInfo implements Serializable {

    int size;

    int number;

    int totalElements;

    int totalPages;

}
