package ai.softeer.caecae.findinggame.domain.dto.response;

import lombok.Builder;

@Builder
public record RegisterWinnerResponseDto(
    boolean success
) {
}
