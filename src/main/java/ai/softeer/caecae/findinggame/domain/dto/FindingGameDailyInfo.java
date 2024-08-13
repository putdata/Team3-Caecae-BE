package ai.softeer.caecae.findinggame.domain.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FindingGameDailyInfo(
        LocalDateTime startTime,
        LocalDateTime endTime,
        int numberOfWinners
) {
}
