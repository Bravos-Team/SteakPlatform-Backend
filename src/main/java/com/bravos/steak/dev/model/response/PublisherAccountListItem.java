package com.bravos.steak.dev.model.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PublisherAccountListItem implements Serializable {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}