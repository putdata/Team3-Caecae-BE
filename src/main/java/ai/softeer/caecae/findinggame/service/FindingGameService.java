package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.FindingGameDailyInfo;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import ai.softeer.caecae.findinggame.repository.FindGameDbRepository;
import ai.softeer.caecae.racinggame.domain.dto.request.RegisterFindingGamePeriodRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.response.FindingGameInfoResponseDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RegisterFindingGamePeriodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindingGameService {
    private final FindGameDbRepository findGameDbRepository;

    /**
     * 어드민이 숨은캐스퍼찾기 게임 기간을 등록하는 로직
     *
     * @param req 게임 시작 날짜
     * @return 게임 시작 날짜, 종료 날짜(+6일)
     */
    @Transactional
    public RegisterFindingGamePeriodResponseDto registerFindingGamePeriod(RegisterFindingGamePeriodRequestDto req) {
        List<FindingGame> findingGames = findGameDbRepository.findAll();
        // 등록된 게임 정보가 없으면 생성하기
        if (findingGames.isEmpty()) {
            findingGames = initFindingGames();
        }

        // 게임 정보 기간 업데이트
        LocalDate date = req.startDate();
        for (FindingGame findingGame : findingGames) {
            findingGame.updateFindingGamePeriod(
                    date.atTime(15, 15),
                    date.plusDays(1).atTime(14, 15)
            );
            date = date.plusDays(1);
        }


        findGameDbRepository.saveAll(findingGames);

        return RegisterFindingGamePeriodResponseDto.builder()
                .startDate(req.startDate())
                .endDate(req.startDate().plusDays(6))
                .build();
    }

    // 7개의 숨은캐스퍼찾기 게임 정보 객체 초기화
    private List<FindingGame> initFindingGames() {
        List<FindingGame> findingGames = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            findingGames.add(
                    FindingGame.builder()
                            .questionImageUrl("no-image")
                            .numberOfWinners(315)
                            .answerType(AnswerType.UNSELECTED)
                            .build());
        }
        return findingGames;
    }

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
            if (findingGame.getStartTime().isBefore(LocalDateTime.now())) {
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
}
