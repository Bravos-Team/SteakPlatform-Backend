package com.bravos.steak.dev.model.response;

import java.util.Collection;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PublisherPermissionItem {

    Long id;

    String name;

    String description;

    Collection<String> authorities;

}
