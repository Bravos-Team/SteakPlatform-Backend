package com.bravos.steak.dev.entity.gamesubmission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildInfo {

    @Field("versionName")
    private String versionName;

    @Field("execPath")
    private String execPath;

    @Field("downloadUrl")
    private String downloadUrl;

    @Field("checksum")
    private String checksum;

}
