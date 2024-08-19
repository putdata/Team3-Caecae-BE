package ai.softeer.caecae.global.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

class SystemTimeConvertorTest {
    @Test
    void convertToLocalDateTime() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 29, 15, 15);
        long epochMilli = dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();

        // when
        LocalDateTime convertedToLocalDateTime = SystemTimeConvertor.convertToLocalDateTime(epochMilli);

        // then
        Assertions.assertThat(convertedToLocalDateTime).isEqualTo(dateTime);
    }
}