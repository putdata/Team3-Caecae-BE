package ai.softeer.caecae.findinggame.domain.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AnswerResponseDto(
        List<CorrectAnswerDto> correctAnswerList,
        String ticketId,
        Long startTime
) {
}
