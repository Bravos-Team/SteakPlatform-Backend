package com.bravos.steak.common.service.snowflake;

import com.bravos.steak.common.service.helper.DateTimeHelper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SnowflakeGenerator {

    private static final long MACHINE_ID_BITS = 10;
    private static final long SEQUENCE_BITS = 12;
    private static final long TIME_STAMP_BITS = 41;
    private static final long EPOCH =
            LocalDateTime.of(2025,1,1,0,0).toInstant(ZoneOffset.UTC).toEpochMilli();

    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS + TIME_STAMP_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS;

    private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1;

    private final long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeGenerator(long machineId) {
        if(machineId < 0 || ((machineId > (1L << MACHINE_ID_BITS) - 1))) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + ((1L << MACHINE_ID_BITS) - 1));
        }
        this.machineId = machineId;
    }

    private long waitForNextMillis() {
        long currentTimeMillis = DateTimeHelper.currentTimeMillis();
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = DateTimeHelper.currentTimeMillis();
        }
        return currentTimeMillis;
    }

    public synchronized long generateId() {
        long currentTimestamp = DateTimeHelper.currentTimeMillis();
        long timestamp = currentTimestamp - EPOCH;
        if(currentTimestamp != lastTimestamp) {
            sequence = 0L;
            lastTimestamp = currentTimestamp;
        }
        else if(sequence >= SEQUENCE_MASK) {
            long nextMillis = waitForNextMillis();
            timestamp = nextMillis - EPOCH;
            sequence = 0L;
            lastTimestamp = nextMillis;
        } else {
            sequence++;
        }
        long timestampShifted = timestamp << TIMESTAMP_SHIFT;
        long machineIdShifted = machineId << MACHINE_ID_SHIFT;
        return timestampShifted | machineIdShifted | sequence;
    }

    public static long extractTimestamp(long id) {
        return ((id >> TIMESTAMP_SHIFT) &
                ((1L << TIME_STAMP_BITS) - 1)) + EPOCH;
    }

    public static long extractMachineId(long id) {
        return (id >> MACHINE_ID_SHIFT) &
                ((1L << MACHINE_ID_BITS) - 1);
    }

}
