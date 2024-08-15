package ai.softeer.caecae.findinggame.domain.dto.response;

import lombok.Builder;

@Builder
public record CorrectAnswerDto(
        double positionX,
        double positionY,
        String descriptionImageUrl,
        String title,
        String content
) {
}
