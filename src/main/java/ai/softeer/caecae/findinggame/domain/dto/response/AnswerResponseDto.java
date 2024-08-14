package ai.softeer.caecae.findinggame.domain.dto.response;

import ai.softeer.caecae.findinggame.domain.dto.request.CoordDto;
import lombok.Builder;

import java.util.List;

@Builder
public record AnswerResponseDto(
        List<CoordDto> correctAnswerList,
        String ticketId,
        Long startTime
) {
}
