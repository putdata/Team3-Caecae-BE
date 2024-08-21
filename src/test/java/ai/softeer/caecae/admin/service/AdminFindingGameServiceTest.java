package ai.softeer.caecae.admin.service;

import ai.softeer.caecae.admin.domain.dto.FindingGameAnswerDto;
import ai.softeer.caecae.admin.domain.dto.request.FindingGameDailyAnswerRequestDto;
import ai.softeer.caecae.admin.domain.dto.response.FindingGameDailyAnswerResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import ai.softeer.caecae.findinggame.repository.FindingGameAnswerDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.racinggame.domain.dto.request.RegisterFindingGamePeriodRequestDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RegisterFindingGamePeriodResponseDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AdminFindingGameServiceTest {

    @InjectMocks
    private AdminFindingGameService adminFindingGameService;

    @Mock
    private FindingGameDbRepository findingGameDbRepository;

    @Mock
    private FindingGameAnswerDbRepository findingGameAnswerDbRepository;

    private Instant fixedInstant = LocalDateTime.of(2024, 8, 10, 10, 10)
            .atZone(ZoneId.systemDefault())
            .toInstant();

    Clock fixClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

    @Test
    @DisplayName("Given_슘캐찾 게임 등록 When_숨캐찾 날짜별 정답 수정 Then_수정된 정답으로 변경되었는지 확인")
    void saveFindingGameDailyAnswer() {
        // given
        // 수정할 정답 정보
        List<FindingGameAnswerDto> findingGameAnswerDtoList = new ArrayList<>();
        for (int i = 6; i <= 7; i++) {
            findingGameAnswerDtoList.add(FindingGameAnswerDto.builder()
                    .positionX(0.1 * i)
                    .positionY(0.1 * i)
                    .descriptionImageUrl("changed-description")
                    .title("changed-title")
                    .content("changed-content")
                    .build()
            );
        }

        // 수정할 정답 정보
        FindingGameDailyAnswerRequestDto dailyAnswerRequestDto = FindingGameDailyAnswerRequestDto.builder()
                .dayOfEvent(1)
                .numberOfWinner(215)
                .startTime(LocalTime.of(11, 49))
                .endTime(LocalTime.of(10, 49))
                .answerType(AnswerType.BADGE)
                .questionImageUrl("image-changed")
                .answerInfoList(findingGameAnswerDtoList)
                .build();

        // 첫째날 숨캐찾 게임 정보
        FindingGame findingGame = FindingGame.builder()
                .id(1) // PK를 1~7로 고정
                .questionImageUrl("no-image")
                .startTime(LocalDateTime.of(2024, 8, 10, 20, 0))
                .endTime(LocalDateTime.of(2024, 8, 11, 19, 0))
                .numberOfWinners(315)
                .answerType(AnswerType.UNSELECTED)
                .build();

        // 첫쨰날 이미 등록된 숨캐찾 게임 정보
        Mockito.when(findingGameDbRepository.findById(1)).thenReturn(Optional.ofNullable(findingGame));

        // 바꾼 정보
        Mockito.when(findingGameDbRepository.save(Mockito.any())).thenReturn(findingGame);


        // when
        FindingGameDailyAnswerResponseDto res = adminFindingGameService.saveFindingGameDailyAnswer(dailyAnswerRequestDto);

        // then
        Assertions.assertThat(res.answerType()).isEqualTo(AnswerType.BADGE);
        Assertions.assertThat(res.questionImageUrl()).isEqualTo("image-changed");
        Assertions.assertThat(res.answerInfoList().get(0).positionX()).isEqualTo(0.6, Offset.offset(0.01d));
        Assertions.assertThat(res.answerInfoList().get(0).positionY()).isEqualTo(0.6, Offset.offset(0.01d));
        Assertions.assertThat(res.answerInfoList().get(1).positionX()).isEqualTo(0.7, Offset.offset(0.01d));
        Assertions.assertThat(res.answerInfoList().get(1).positionY()).isEqualTo(0.7, Offset.offset(0.01d));
        Assertions.assertThat(res.startTime().getHour()).isEqualTo(11);
        Assertions.assertThat(res.endTime().getHour()).isEqualTo(10);
        Assertions.assertThat(res.numberOfWinner()).isEqualTo(215);

    }

    @Test
    @DisplayName("Given_숨캐찾 게임 미등록 상태 When_숨캐찾 게임 기간 등록 Then_숨캐찾 기간이 등록되는지 확인")
    void registerFindingGamePeriod() {
        // given
        LocalDate startDate = LocalDate.now(fixClock);

        RegisterFindingGamePeriodRequestDto req = RegisterFindingGamePeriodRequestDto.builder()
                .startDate(startDate)
                .build();

        // 현재 게임 미존재
        Mockito.when(findingGameDbRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        RegisterFindingGamePeriodResponseDto res = adminFindingGameService.registerFindingGamePeriod(req);

        // then
        Assertions.assertThat(res.startDate()).isEqualTo(startDate);
        Assertions.assertThat(res.endDate()).isEqualTo(startDate.plusDays(6));
    }
}