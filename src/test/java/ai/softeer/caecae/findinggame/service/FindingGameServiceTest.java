package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.response.FindingGameInfoResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.TimeZone;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindingGameServiceTest {

    @InjectMocks
    private FindingGameService findingGameService;

    @Mock
    private FindingGameDbRepository findGameDbRepository;

    @Mock
    private Clock clock;

    // 7일치의 숨은그림찾기 게임 정보
    private final List<FindingGame> findingGames = new ArrayList<>();
    private static LocalDateTime integrityDateTime;


    @BeforeAll
    static void timeSetUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        integrityDateTime = LocalDateTime.of(2024, 5, 5, 10, 10);
    }

    @BeforeEach
    void setUp() {
        // 8/1 ~ 8/7 게임
        LocalDateTime baseTime = LocalDateTime.of(2024, 8, 1, 15, 15);
        for (int day = 0; day < 7; day++) {
            findingGames.add(
                    FindingGame.builder()
                            .id(day + 1)
                            .questionImageUrl("url")
                            .startTime(baseTime.plusDays(day))
                            .endTime(baseTime.plusDays(day + 1).minusHours(1))
                            .numberOfWinners(315)
                            .answerType(AnswerType.PIXEL)
                            .build()
            );
        }

        Mockito.when(findGameDbRepository.findAllByOrderByStartTime()).thenReturn(findingGames);

        given(clock.getZone()).willReturn(ZoneId.systemDefault());
    }

    @Test
    @DisplayName("LocalDateTime과 비교하여, Clock.instant의 시간에 TimeZone의 시차가 적용되는지 테스트 하기 - 같은 시간")
    void testUtcClockInstant1() {
        // Given
        given(clock.instant()).willReturn(Instant.parse("2024-05-05T10:10:00Z"));

        // When
        // Clock을 사용하여 LocalDateTime 생성
        LocalDateTime time = findingGameService.testUtcClockInstant();

        // Then
        Assertions.assertThat(time).isNotEqualTo(integrityDateTime);
    }

    @Test
    @DisplayName("LocalDateTime과 Clock.instant의 시간에 UTC+9의 시차가 적용되는지 테스트 하기 - UTC+9")
    void testUtcClockInstant2() {
        // Given
        given(clock.instant()).willReturn(Instant.parse("2024-05-05T10:10:00Z").minusSeconds(9 * 3600));

        // When
        // Clock을 사용하여 LocalDateTime 생성
        LocalDateTime time = findingGameService.testUtcClockInstant();

        // Then
        Assertions.assertThat(time).isEqualTo(integrityDateTime);
    }


    @Test
    @DisplayName("Given_일주일치 게임 정보 입력 When_게임 시작 전 Then_최근게임 인덱스가 -1")
    void getFindingGameInfo_1() {
        // Given
        // LocalDateTime.now()에서 사용할 clock 객체 모킹
        given(clock.instant()).willReturn(Instant.parse("2024-08-01T09:00:00Z").minusSeconds(9 * 3600));

        // When
        FindingGameInfoResponseDto findingGameInfo = findingGameService.getFindingGameInfo();

        // Then
        Assertions.assertThat(findingGameInfo).isNotNull();
        Assertions.assertThat(findingGameInfo.recentGameIndex()).isEqualTo(-1);
        Assertions.assertThat(findingGameInfo.nextGameIndex()).isEqualTo(0);
    }

    @Test
    @DisplayName("Given_일주일치 게임 정보 입력 When_게임 진행 중(첫날) Then_최근게임 인덱스가 0")
    void getFindingGameInfo_2() {
        // Given
        // LocalDateTime.now()에서 사용할 clock 객체 모킹
        given(clock.instant()).willReturn(Instant.parse("2024-08-01T17:00:00Z").minusSeconds(9 * 3600));

        // When
        FindingGameInfoResponseDto findingGameInfo = findingGameService.getFindingGameInfo();

        // Then
        Assertions.assertThat(findingGameInfo).isNotNull();
        Assertions.assertThat(findingGameInfo.recentGameIndex()).isEqualTo(0);
        Assertions.assertThat(findingGameInfo.nextGameIndex()).isEqualTo(1);
    }

    @Test
    @DisplayName("Given_일주일치 게임 정보 입력 When_게임 진행 중(마지막날) Then_최근게임 인덱스가 6")
    void getFindingGameInfo_3() {
        // Given
        // LocalDateTime.now()에서 사용할 clock 객체 모킹
        given(clock.instant()).willReturn(Instant.parse("2024-08-08T10:00:00Z").minusSeconds(9 * 3600));

        // When
        FindingGameInfoResponseDto findingGameInfo = findingGameService.getFindingGameInfo();

        // Then
        Assertions.assertThat(findingGameInfo).isNotNull();
        Assertions.assertThat(findingGameInfo.recentGameIndex()).isEqualTo(6);
        Assertions.assertThat(findingGameInfo.nextGameIndex()).isEqualTo(-1);
    }

    @Test
    @DisplayName("Given_일주일치 게임 정보 입력 When_게임 진행 종료 Then_다음게임 인덱스가 -1")
    void getFindingGameInfo_4() {
        // Given
        // LocalDateTime.now()에서 사용할 clock 객체 모킹
        given(clock.instant()).willReturn(Instant.parse("2032-08-08T16:00:00Z").minusSeconds(9 * 3600));

        // When
        FindingGameInfoResponseDto findingGameInfo = findingGameService.getFindingGameInfo();

        // Then
        Assertions.assertThat(findingGameInfo).isNotNull();
        Assertions.assertThat(findingGameInfo.recentGameIndex()).isEqualTo(6);
        Assertions.assertThat(findingGameInfo.nextGameIndex()).isEqualTo(-1);
    }
}