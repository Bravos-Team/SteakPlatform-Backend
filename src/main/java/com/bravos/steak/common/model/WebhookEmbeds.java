package com.bravos.steak.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEmbeds {

    private String title;

    private String type = "rich";

    private String description;

    private String color;

}
