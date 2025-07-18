package com.bravos.steak.dev.repo.injection;

import com.bravos.steak.dev.entity.PublisherRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PublisherAccountInfoListItem {

    private Long id;
    private String username;
    private String email;
    private Set<PublisherRole> roles;

}
