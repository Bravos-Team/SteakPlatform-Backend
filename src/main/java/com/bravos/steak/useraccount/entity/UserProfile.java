package com.bravos.steak.useraccount.entity;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("UserProfile")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @NonNull
    private Long id;

    private String displayName;

    private String birthDate;

    private Boolean sex;

    private String avatarUrl;

    private String bio;

}
