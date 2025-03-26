package com.bravos.steak.account.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseObject<T>{
    String status;
    String message;
    T data;
    LocalDateTime currentTimeLog;
    public ResponseObject(String status, String message, T data){
        this.message = message;
        this.data = data;
        this.status = status;
        this.currentTimeLog = LocalDateTime.now();
    }

}
