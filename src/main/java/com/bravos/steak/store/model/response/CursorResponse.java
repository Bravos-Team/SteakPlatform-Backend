package com.bravos.steak.store.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CursorResponse<T> {

    Collection<T> items;

    Long maxCursor;

    Boolean hasNextCursor;

    public static <T> CursorResponse<T> empty() {
        return CursorResponse.<T>builder()
                .items(List.of())
                .maxCursor(null)
                .hasNextCursor(false)
                .build();
    }

}
