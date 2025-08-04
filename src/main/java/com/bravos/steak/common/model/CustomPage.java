package com.bravos.steak.common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CustomPage<T> implements Serializable {

    List<T> content;

    CustomPageInfo page;

    public CustomPage(Page<T> page) {
        this.content = page.getContent();
        this.page = CustomPageInfo.builder()
                .size(page.getSize())
                .number(page.getNumber())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

}
