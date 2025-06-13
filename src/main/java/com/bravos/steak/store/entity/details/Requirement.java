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
public class Requirement {

    @Field("osVersion")
    private String osVersion;

    @Field("cpu")
    private String cpu;

    @Field("memory")
    private String memory;

    @Field("gpu")
    private String gpu;

    @Field("directX")
    private String directX;

    @Field("storage")
    private String storage;

}
