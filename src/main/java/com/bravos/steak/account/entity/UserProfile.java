package com.bravos.steak.account.entity;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document("user_profile")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @NonNull
    private Long id;

    @Field(name = "display_name")
    private String displayName;

    @Field(name = "birth_date")
    private LocalDate birthDate;

    @Field(name = "sex")
    private Boolean sex;

    @Field(name = "avatar_url")
    private String avatarUrl;

}
