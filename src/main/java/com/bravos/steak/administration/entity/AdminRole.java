package com.bravos.steak.administration.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_role")
public class AdminRole {

    @Id
    Long id;

    String name;

    @Builder.Default
    Boolean active = true;

    String description;

    Long updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_permission_role",
            joinColumns = @JoinColumn(name = "admin_role_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_permission_id")
    )
    Set<AdminPermission> adminPermissions;

    public AdminRole(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AdminRole that = (AdminRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
