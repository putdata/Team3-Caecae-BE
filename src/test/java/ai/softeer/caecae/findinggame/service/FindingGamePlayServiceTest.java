package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.PositionDto;
import ai.softeer.caecae.findinggame.domain.dto.request.AnswerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.request.HintRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.response.AnswerResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameAnswer;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import ai.softeer.caecae.findinggame.domain.exception.FindingGameException;
import ai.softeer.caecae.findinggame.repository.FindingGameAnswerDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FindingGamePlayServiceTest {


    @InjectMocks
    private FindingGamePlayService findingGamePlayService;

    @Mock
    private FindingGameRedisRepository findingGameRedisRepository;

    @Mock
    private FindingGameDbRepository findingGameDbRepository;

    @Mock
    private FindingGameAnswerDbRepository findingGameAnswerDbRepository;

    @Mock
    private Clock clock;

    private Instant fixedInstant = LocalDateTime.of(2024, 8, 10, 10, 10)
            .atZone(ZoneId.systemDefault())
            .toInstant();

    // 사용자가 힌트의 요청으로 보낸 정답
    private List<PositionDto> userAnswers;

    private List<FindingGame> findingGames;

    @BeforeEach
    void setUp() {
        // 숨캐찾 게임 초기화
        findingGames = initFindingGames();

        // 숨캐찾 정답 초기화
        Mockito.when(findingGameAnswerDbRepository.findAllByFindingGame_Id(Mockito.anyInt()))
                .thenReturn(initFindingGameAnswer(null));
    }

    private List<FindingGame> initFindingGames() {
        // 원하는 날짜와 시간을 사용하여 Clock을 고정
        Clock fixClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        List<FindingGame> findingGames = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(fixClock); // 고정된 clock 사용

        for (int day = 0; day < 7; day++) {
            findingGames.add(
                    FindingGame.builder()
                            .id(day + 1) // PK를 1~7로 고정
                            .questionImageUrl("no-image")
                            .startTime(now.plusDays(day))
                            .endTime(now.plusDays(day + 1).minusHours(1))
                            .numberOfWinners(315)
                            .answerType(AnswerType.UNSELECTED)
                            .build()
            );
        }
        return findingGames;
    }

    private List<FindingGameAnswer> initFindingGameAnswer(FindingGame findingGame) {
        List<FindingGameAnswer> findingGameAnswerList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            findingGameAnswerList.add(FindingGameAnswer.builder()
                    .positionX(0.1 * i)
                    .positionY(0.1 * i)
                    .descriptionImageUrl("no-image")
                    .title("no-title")
                    .content("no-content")
                    .findingGame(findingGame)
                    .build()
            );
        }
        return findingGameAnswerList;
    }

    @Test
    @DisplayName("Given_숨은캐스퍼찾기 When_정답을 못맞추고 힌트를 요청 Then_첫 번째 정답 반환")
    void getHint_0() {
        // given
        // 사용자의 정답
        userAnswers = new ArrayList<>();

        HintRequestDto hintRequestDto = HintRequestDto.builder()
                .answerList(userAnswers)
                .build();

        Mockito.when(findingGameDbRepository.findAllOrderByStartTimeCacheable()).thenReturn(findingGames);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(fixedInstant);

        // when
        PositionDto hintDto = findingGamePlayService.getHint(hintRequestDto).hintPosition();

        // then
        Assertions.assertThat(hintDto.positionX()).isEqualTo(0.1);
        Assertions.assertThat(hintDto.positionY()).isEqualTo(0.1);
    }

    @Test
    @DisplayName("Given_숨은캐스퍼찾기 When_정답을 1개 맞추고 힌트 요청 Then_두 번째 정답 반환")
    void getHint_1() {
        // given
        // 사용자의 정답
        userAnswers = new ArrayList<>();
        userAnswers.add(PositionDto.builder()
                .positionX(0.11)
                .positionY(0.11)
                .build()
        );
        HintRequestDto hintRequestDto = HintRequestDto.builder()
                .answerList(userAnswers)
                .build();

        Mockito.when(findingGameDbRepository.findAllOrderByStartTimeCacheable()).thenReturn(findingGames);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(fixedInstant);

        // when
        PositionDto hintDto = findingGamePlayService.getHint(hintRequestDto).hintPosition();

        // then
        Assertions.assertThat(hintDto.positionX()).isEqualTo(0.2, Offset.offset(0.01d));
        Assertions.assertThat(hintDto.positionY()).isEqualTo(0.2, Offset.offset(0.01d));
    }

    @Test
    @DisplayName("Given_숨은캐스퍼찾기 When_정답을 2개 맞추고 힌트 요청 Then_예외 던지기")
    void getHint_2_Exception() {
        // given
        // 사용자의 정답
        userAnswers = new ArrayList<>();
        userAnswers.add(PositionDto.builder()
                .positionX(0.11)
                .positionY(0.11)
                .build()
        );
        userAnswers.add(PositionDto.builder()
                .positionX(0.11)
                .positionY(0.11)
                .build()
        );

        HintRequestDto hintRequestDto = HintRequestDto.builder()
                .answerList(userAnswers)
                .build();

        Mockito.when(findingGameDbRepository.findAllOrderByStartTimeCacheable()).thenReturn(findingGames);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(fixedInstant);

        // when & then
        Assertions.assertThatThrownBy(() -> findingGamePlayService.getHint(hintRequestDto).hintPosition())
                .isInstanceOf(FindingGameException.class);
    }

    @Test
    @DisplayName("Given_사용자가 정답을 맞춤 When_315명 안에 들지 못함 Then_실패 응답 반환하기")
    void checkAnswer_316() {
        // given
        // 사용자의 정답
        userAnswers = new ArrayList<>();
        userAnswers.add(PositionDto.builder()
                .positionX(0.11)
                .positionY(0.11)
                .build()
        );
        userAnswers.add(PositionDto.builder()
                .positionX(0.22)
                .positionY(0.22)
                .build()
        );

        AnswerRequestDto answerRequestDto = AnswerRequestDto.builder()
                .answerList(userAnswers)
                .build();

        Mockito.when(findingGameDbRepository.findAllOrderByStartTimeCacheable()).thenReturn(findingGames);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(fixedInstant);

        // 316번째로 정답을 맞춘 경우
        Mockito.when(findingGameRedisRepository.increaseCount()).thenReturn(316L);

        // when
        AnswerResponseDto answerResponseDto = findingGamePlayService.checkAnswer(answerRequestDto);

        Assertions.assertThat(answerResponseDto.startTime()).isEqualTo(-1L);
        Assertions.assertThat(answerResponseDto.ticketId()).isEqualTo("");
    }

    @Test
    @DisplayName("Given_사용자가 정답을 맞춤 When_315명 안에 들음 Then_성공 응답 반환하기")
    void checkAnswer_315() {

        // given
        // 사용자의 정답
        userAnswers = new ArrayList<>();
        userAnswers.add(PositionDto.builder()
                .positionX(0.11)
                .positionY(0.11)
                .build()
        );
        userAnswers.add(PositionDto.builder()
                .positionX(0.22)
                .positionY(0.22)
                .build()
        );

        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        AnswerRequestDto answerRequestDto = AnswerRequestDto.builder()
                .answerList(userAnswers)
                .build();

        Mockito.when(findingGameDbRepository.findAllOrderByStartTimeCacheable()).thenReturn(findingGames);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(fixedInstant);

        // 316번째로 정답을 맞춘 경우
        Mockito.when(findingGameRedisRepository.increaseCount()).thenReturn(315L);

        Mockito.when(findingGameRedisRepository.addWinner(Mockito.anyString()))
                .thenReturn(fixedClock.millis());

        // when
        AnswerResponseDto answerResponseDto = findingGamePlayService.checkAnswer(answerRequestDto);

        Assertions.assertThat(answerResponseDto.startTime()).isEqualTo(fixedClock.millis());
        Assertions.assertThat(answerResponseDto.ticketId()).isNotEqualTo("");
    }
}
