package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.FindingGameDailyInfo;
import ai.softeer.caecae.findinggame.domain.dto.response.FindingGameInfoResponseDto;
import ai.softeer.caecae.findinggame.domain.dto.response.StartGameResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingGameService {
    private final Clock clock; // 테스트코드 의존성 주입을 위한 빈
    private final FindingGameDbRepository findingGameDbRepository;

    /**
     * 전체 게임 정보와, 최근/다음 게임의 인덱스를 반환하는 로직
     *
     * @return 전체 게임 정보, 최근/다음 게임의 인덱스
     */
    @Cacheable(cacheNames = "FindingGameInfo")
    @Transactional(readOnly = true)
    public FindingGameInfoResponseDto getFindingGameInfo() {
        List<FindingGame> findingGames = findingGameDbRepository.findAllByOrderByStartTime();
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

    /**
     * 현재 게임이 시작가능하고 가능하다면 관련 정보를 넘겨주는 서비스 로직
     *
     * @return
     */
    public StartGameResponseDto startFindingGame() {
        FindingGameInfoResponseDto dto = getFindingGameInfo(); // TODO: 같은 class내에 있어서 캐싱이 안되는 문제 있음
        int recentGameIndex = Math.max(dto.recentGameIndex(), 0), nextGameIndex = dto.nextGameIndex();

        HashMap<String, String> infoHashMap = new HashMap<>();

        //TODO: nextGameIndex == -1 이면 에러 수정
        FindingGameDailyInfo nextGameInfo = dto.findingGameInfos().get(nextGameIndex);
        LocalDateTime nowTime = LocalDateTime.now(clock);
        // 캐싱으로 인해 다음 게임이 이미 시작된 경우를 확인
        if (nextGameInfo.startTime().isBefore(nowTime) && nextGameInfo.endTime().isBefore(nowTime)) {
            recentGameIndex = nextGameIndex;
        }

        FindingGameDailyInfo gameInfo = dto.findingGameInfos().get(recentGameIndex);
        // 게임 가능 시간 사이에 들지 않으면
        if (!(gameInfo.startTime().isBefore(nowTime) && gameInfo.endTime().isAfter(nowTime))) {
            return StartGameResponseDto.builder()
                    .availiable(false)
                    .info(infoHashMap)
                    .build();
        }
        FindingGame findingGame = findingGameDbRepository.findByIdCacheable(recentGameIndex + 1);
        infoHashMap.put("questionImageUrl", findingGame.getQuestionImageUrl());
        infoHashMap.put("answerType", findingGame.getAnswerType().toString());
        return StartGameResponseDto.builder()
                .availiable(true)
                .info(infoHashMap)
                .build();
    }

    // 테스트용
    public LocalDateTime testUtcClockInstant() {
        findingGameDbRepository.findAllByOrderByStartTime();
        return LocalDateTime.now(clock);
    }

}

