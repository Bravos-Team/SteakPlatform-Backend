package com.bravos.steak.useraccount.entity;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("UserProfile")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @NonNull
    private Long id;

    private String displayName;

    private LocalDate birthDate;

    private Boolean sex;

    private String avatarUrl;

}
