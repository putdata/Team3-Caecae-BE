package ai.softeer.caecae.findinggame.domain.dto.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record StartGameResponseDto(
        boolean availiable,
        Map<String, String> info
) {
}
