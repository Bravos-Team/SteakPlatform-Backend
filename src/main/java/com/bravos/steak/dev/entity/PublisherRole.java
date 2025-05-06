package com.bravos.steak.dev.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "publisher_role")
public class PublisherRole {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    Publisher publisher;

    String name;

    @Builder.Default
    Boolean active = true;

    String description;

    LocalDateTime updatedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "publisher_permission_role",
            joinColumns = @JoinColumn(name = "publisher_role_id"),
            inverseJoinColumns = @JoinColumn(name = "publisher_permission_id")
    )
    Set<PublisherPermission> publisherPermissions;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PublisherRole that = (PublisherRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
