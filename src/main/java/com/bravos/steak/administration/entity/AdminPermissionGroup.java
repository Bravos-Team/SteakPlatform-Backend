package com.bravos.steak.administration.entity;

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
public class AdminPermissionGroup {

    @Id
    Long id;

    String name;

    String description;

    @OneToMany(mappedBy = "permissionGroup", cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    List<AdminPermission> adminPermissionList;

}
