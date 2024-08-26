package ai.softeer.caecae.admin.domain.dto;

import lombok.Builder;

@Builder
public record FindingGameWinnerDto(
        int day,
        String phone
) {
}
