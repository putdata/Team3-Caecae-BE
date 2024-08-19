package ai.softeer.caecae.global.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SystemTimeConvertor {
    public static LocalDateTime convertToLocalDateTime(Long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }
}
