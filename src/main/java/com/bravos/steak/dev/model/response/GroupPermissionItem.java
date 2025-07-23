package com.bravos.steak.dev.model.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GroupPermissionItem {

    Long id;

    String name;

    String description;

    List<PublisherPermissionListItem> permissions;

}
