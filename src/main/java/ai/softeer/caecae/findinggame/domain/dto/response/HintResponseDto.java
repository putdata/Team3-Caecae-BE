package ai.softeer.caecae.findinggame.domain.dto.response;

import ai.softeer.caecae.findinggame.domain.dto.PositionDto;
import lombok.Builder;

@Builder
public record HintResponseDto(
        PositionDto hintPosition
) {

}
