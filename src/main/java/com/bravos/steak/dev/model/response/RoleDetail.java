package com.bravos.steak.dev.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class RoleDetail {

    Long id;

    String name;

    String description;

    Boolean isActive;

    List<PublisherAccountListItem> assignedAccounts;

}
