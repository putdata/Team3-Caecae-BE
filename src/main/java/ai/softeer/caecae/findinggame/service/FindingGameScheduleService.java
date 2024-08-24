package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameRealWinner;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.global.utils.SystemTimeConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingGameScheduleService {
    private final FindingGameRedisRepository findingGameRedisRepository;
    private final FindingGamePlayService findingGamePlayService;
    private final FindingGameDbRepository findingGameDbRepository;
    private final Clock clock;

    private final int TRANSACTION_COUNT = 50;

    @Scheduled(cron = "*/1 * * * * *")
    public void insertWinnerToDatabaseScheduler() {
        int completed = 0;
        List<FindingGameWinner> winners = new ArrayList<>();
        for (int i = 0; i < TRANSACTION_COUNT; i++) {
            FindingGameRealWinner realWinner = findingGameRedisRepository.getFrontRealWinner();
            if (realWinner == null) break;
            Integer gameId = realWinner.getGameId();
            String phone = realWinner.getPhone();
            try {
                findingGamePlayService.insertWinner(gameId, phone);
            } catch (Exception e) {
                log.error("선착순 인원 처리 에러 - gameId:{}, phone:{}, 처리 완료된 건: {}", gameId, phone, completed);
                return;
            }
            findingGameRedisRepository.popRealWinner();
            completed++;
        }
        if (completed > 0) {
            log.info("선착순 인원 스케쥴링 처리 완료 - {} 건", completed);
        }
    }

    /**
     * 정답을 모두 맞추었지만 전화번호를 입력하지 않은 winner의 데이터를 주기적으로 삭제한다.
     */
    @Scheduled(cron = "0 */5 * * * *") // 매 5분마다 실행
    public void deleteUnregisteredWinner() {
        Map<Object, Object> winners = findingGameRedisRepository.getAllWinner();
        for (Map.Entry<Object, Object> entry : winners.entrySet()) {
            String ticketId = entry.getKey().toString();
            Long startTime = (Long) entry.getValue();
            Long diffTime = System.currentTimeMillis() - startTime;

            if (diffTime > 1000 * 60 * 3) {
                LocalDateTime convertedStartTime = SystemTimeConvertor.convertToLocalDateTime(startTime);
                LocalDateTime now = SystemTimeConvertor.convertToLocalDateTime(System.currentTimeMillis());

                findingGameRedisRepository.deleteWinner(ticketId);
                Long count = findingGameRedisRepository.decreaseCount();

                log.info("[DELETED] UUID : " + ticketId);
                log.info("[DELETED] 정답을 맞춘 시간: " + convertedStartTime);
                log.info("[DELETED] 현재 시간     : " + now);
                log.info("current count : " + count);
            }
        }
    }

    /**
     * 10분마다 count의 값을 0으로 초기화하는 메서드
     */
    @Scheduled(cron = "59 */10 * * * *")
    public void initDailyWinnerCountScheduler() {
        List<FindingGame> findingGames = findingGameDbRepository.findAllOrderByStartTimeCacheable();
        LocalDateTime now = LocalDateTime.now(clock);
        Boolean isFindingGamePlayable = false;

        for (FindingGame findingGame : findingGames) {
            if (findingGame.getStartTime().isBefore(now) && findingGame.getEndTime().isAfter(now)) {
                isFindingGamePlayable = true;
                break;
            }
        }

        if (!isFindingGamePlayable) {
            findingGameRedisRepository.initDailyWinnerCount();
            log.info("Init FindingGame count to 0");
        }

    }


}
