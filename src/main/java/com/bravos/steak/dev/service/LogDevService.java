package com.bravos.steak.dev.service;

public interface LogDevService {

    void saveLog(long devId, long publisherId, String message, Object...args);

}
