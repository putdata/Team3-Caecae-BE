package ai.softeer.caecae.admin.service;

import ai.softeer.caecae.admin.domain.dto.response.RacingGameWinnerResponseDto;
import ai.softeer.caecae.racinggame.domain.entity.RacingGameInfo;
import ai.softeer.caecae.racinggame.domain.entity.RacingGameParticipant;
import ai.softeer.caecae.racinggame.repository.RacingGameInfoRepository;
import ai.softeer.caecae.racinggame.repository.RacingGameRepository;
import ai.softeer.caecae.racinggame.repository.RacingGameWinnerRepository;
import ai.softeer.caecae.user.domain.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdminRacingGameServiceTest {
    @InjectMocks
    private AdminRacingGameService adminRacingGameService;

    @Mock
    private RacingGameRepository racingGameRepository;
    @Mock
    private RacingGameWinnerRepository racingGameWinnerRepository;

    @Mock
    private RacingGameInfoRepository raceGameInfoRepository;


    @ParameterizedTest
    @ValueSource(ints = {1, 50, 315, 10000})
    @DisplayName("Given_참여자가 50명 When_추첨 시작 Then_50명 뽑기 성공")
    void drawRacingGameWinner(int participantsCount) {
        // given
        User user = User.builder()
                .phone("01011112222")
                .build();

        List<RacingGameParticipant> participants = new ArrayList<>();
        for (int i = 0; i < participantsCount; i++) {
            participants.add(
                    RacingGameParticipant.builder()
                            .userId(i)
                            .user(user)
                            .distance(315 + 0.1 * i)
                            .selection(1)
                            .build()
            );
        }

        RacingGameInfo racingGameInfo = RacingGameInfo.builder()
                .numberOfWinners(315)
                .build();

        Mockito.when(racingGameRepository.findAllByAdjustedDistance(315)).thenReturn(participants);
        Mockito.when(raceGameInfoRepository.get()).thenReturn(racingGameInfo);

        // when
        List<RacingGameWinnerResponseDto> racingGameWinnerResponse = adminRacingGameService.drawRacingGameWinner();

        // then
        Assertions.assertThat(racingGameWinnerResponse.size()).isEqualTo(Math.min(315, participantsCount));
    }


}