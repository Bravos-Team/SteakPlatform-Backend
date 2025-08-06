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
public class PlayGameSession {

    long startTime;

    long latestPingTime;

    String deviceId;

}
