package com.bravos.steak.store.entity.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Field(name = "type")
    private String type;

    @Field(name = "url")
    private String url;

}
