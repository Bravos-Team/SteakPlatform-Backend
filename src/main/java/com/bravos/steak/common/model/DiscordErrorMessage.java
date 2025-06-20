package com.bravos.steak.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscordErrorMessage {
    private String title;
    private String details;
    private String exception;
    private String timestamp;
}