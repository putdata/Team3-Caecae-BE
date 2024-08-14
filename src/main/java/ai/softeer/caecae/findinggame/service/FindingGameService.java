package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.FindingGameDailyInfo;
import ai.softeer.caecae.findinggame.domain.dto.response.FindingGameInfoResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.repository.FindGameDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindingGameService {
    private final FindGameDbRepository findGameDbRepository;
    private final Clock clock; // 테스트코드 의존성 주입을 위한 빈
  
    /**
     * 전체 게임 정보와, 최근/다음 게임의 인덱스를 반환하는 로직
     *
     * @return 전체 게임 정보, 최근/다음 게임의 인덱스
     */
    @Transactional(readOnly = true)
    public FindingGameInfoResponseDto getFindingGameInfo() {
        List<FindingGame> findingGames = findGameDbRepository.findAllByOrderByStartTime();
        int recentGameIndex = -1;
        int nextGameIndex = -1;
        for (int i = 0; i < findingGames.size(); i++) {
            FindingGame findingGame = findingGames.get(i);
            if (findingGame.getStartTime().isBefore(LocalDateTime.now(clock))) {
                recentGameIndex = i;
            }
        }

        // 마지막 게임이 아니라면, nextGameIndex 설정
        if (recentGameIndex != findingGames.size() - 1) {
            nextGameIndex = recentGameIndex + 1;
        }

        List<FindingGameDailyInfo> findingGameDailyInfos = new ArrayList<>();
        for (FindingGame findingGame : findingGames) {
            findingGameDailyInfos.add(
                    FindingGameDailyInfo.builder()
                            .startTime(findingGame.getStartTime())
                            .endTime(findingGame.getEndTime())
                            .numberOfWinners(findingGame.getNumberOfWinners())
                            .build()
            );
        }

        return FindingGameInfoResponseDto.builder()
                .findingGameInfos(findingGameDailyInfos)
                .recentGameIndex(recentGameIndex)
                .nextGameIndex(nextGameIndex)
                .build();
    }

    // 테스트용
    public LocalDateTime testUtcClockInstant() {
        findGameDbRepository.findAllByOrderByStartTime();
        return LocalDateTime.now(clock);
    }

}

