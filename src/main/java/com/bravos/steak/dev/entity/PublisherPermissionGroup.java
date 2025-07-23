package com.bravos.steak.dev.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PublisherPermissionGroup {

    @Id
    Long id;

    String name;

    String description;

    @OneToMany(mappedBy = "permissionGroup", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    List<PublisherPermission> publisherPermissionList;

}
