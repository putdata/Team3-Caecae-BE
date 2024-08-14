package ai.softeer.caecae.findinggame.domain.dto.request;

import java.util.List;

public record AnswerRequestDto(
        List<CoordDto> answerList
) {
}
