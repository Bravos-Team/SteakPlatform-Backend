package com.bravos.steak.store.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class TimePlayResponse {

    Long gameId;

    Long playSeconds;

    Long playRecentDate;

}
