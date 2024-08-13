package ai.softeer.caecae.findinggame.domain.dto.response;

import ai.softeer.caecae.findinggame.domain.dto.FindingGameDailyInfo;
import lombok.Builder;

import java.util.List;

// 일주일치 게임 정보
@Builder
public record FindingGameInfoResponseDto(
        List<FindingGameDailyInfo> findingGameInfos,
        int recentGameIndex,
        int nextGameIndex
) {
}
