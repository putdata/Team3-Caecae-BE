package ai.softeer.caecae.admin.domain.dto.response;

import ai.softeer.caecae.admin.domain.dto.FindingGameAnswerDto;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

// 숨은캐스퍼찾기 날짜별 정답 정보 응답 객체
@Builder
public record FindingGameDailyAnswerResponseDto(
        int dayOfEvent,
        int numberOfWinner,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AnswerType answerType,
        String questionImageUrl,
        List<FindingGameAnswerDto> answerInfoList
) {
}
