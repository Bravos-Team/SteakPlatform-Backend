package com.bravos.steak.administration.model.response;

import com.bravos.steak.dev.model.enums.PublisherStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PublisherListItem {

    Long id;

    String name;

    String email;

    PublisherStatus status;

}
