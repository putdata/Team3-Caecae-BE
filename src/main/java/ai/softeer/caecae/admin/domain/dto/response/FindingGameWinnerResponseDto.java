package ai.softeer.caecae.admin.domain.dto.response;

import ai.softeer.caecae.admin.domain.dto.FindingGameWinnerDto;
import lombok.Builder;

import java.util.List;

@Builder
public record FindingGameWinnerResponseDto(
        List<FindingGameWinnerDto> winners
) {
}
