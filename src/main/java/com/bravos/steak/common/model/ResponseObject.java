package com.bravos.steak.account.model.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseObject<T>{

    String status;
    T data;
    LocalDateTime currentTimeLog;

    public ResponseObject(String status, T data){
        this.data = data;
        this.status = status;
        this.currentTimeLog = LocalDateTime.now();
    }

}
