package com.bravos.steak.socket.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameTrackingMessage {

    public static final String START = "START";
    public static final String STOP = "STOP";

    Long userId;

    Long gameId;

    String action;

    String deviceId;

}
