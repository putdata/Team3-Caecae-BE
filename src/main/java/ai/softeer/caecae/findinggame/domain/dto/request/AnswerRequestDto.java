package ai.softeer.caecae.findinggame.domain.dto.request;

import ai.softeer.caecae.findinggame.domain.dto.PositionDto;

import java.util.List;

public record AnswerRequestDto(
        List<PositionDto> answerList
) {
}
