package ai.softeer.caecae.findinggame.domain.dto.request;

import ai.softeer.caecae.findinggame.domain.dto.PositionDto;
import lombok.Builder;

import java.util.List;

@Builder
public record AnswerRequestDto(
        List<PositionDto> answerList
) {
}
