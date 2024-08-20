package ai.softeer.caecae.findinggame.domain.dto;

import lombok.Builder;

@Builder
public record PositionDto(
        double positionX,
        double positionY
) {
}
