package ai.softeer.caecae.findinggame.repository;

import ai.softeer.caecae.findinggame.domain.entity.FindingGameRealWinner;
import ai.softeer.caecae.global.utils.SystemTimeConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FindingGameRedisRepository {
    private final static String REAL_WINNER_KEY = "real_winner";
    private final static String COUNT_KEY = "count";
    private final static String WINNER_KEY = "winner";
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 당첨자 수 증가
     *
     * @return 현재 당첨자 수 값
     */
    public Long increaseCount() {
        return redisTemplate.opsForValue().increment(COUNT_KEY);
    }

    /**
     * 당첨자 수 감소
     *
     * @return 현재 당첨자 수 값
     */
    public Long decreaseCount() {
        return redisTemplate.opsForValue().decrement(COUNT_KEY);
    }


    /**
     * 선착순에 들은 참여자를 위너 목록에 추가한다.
     *
     * @param ticketId
     * @return 시작 시간
     */
    public Long addWinner(String ticketId) {
        Long startTime = System.currentTimeMillis();
        redisTemplate.opsForHash().put(WINNER_KEY, ticketId, startTime);
        return startTime;
    }

    /**
     * 위너 목록에서 참여자를 삭제한다.
     *
     * @param ticketId
     */
    public void deleteWinner(String ticketId) {
        redisTemplate.opsForHash().delete(WINNER_KEY, ticketId);
    }

    /**
     * TicketID를 가진 참여자의 입력 시작 시간을 가져온다.
     *
     * @param ticketId
     * @return
     */
    public Long getWinnerStartTime(String ticketId) {
        return (Long) redisTemplate.opsForHash().get(WINNER_KEY, ticketId);
    }

    /**
     * 레디스 큐에 DB에 반영될 참여자 PUSH
     *
     * @param winner
     */
    public void pushRealWinner(FindingGameRealWinner winner) {
        redisTemplate.opsForList().rightPush(REAL_WINNER_KEY, winner);
    }

    /**
     * 레디스 큐에서 DB에 반영된 참여자 POP
     *
     * @return
     */
    public FindingGameRealWinner popRealWinner() {
        return (FindingGameRealWinner) redisTemplate.opsForList().leftPop(REAL_WINNER_KEY);
    }

    /**
     * 레디스 큐에서 DB에 반영될 참여자 GET
     *
     * @return
     */
    public FindingGameRealWinner getFrontRealWinner() {
        return (FindingGameRealWinner) redisTemplate.opsForList().index(REAL_WINNER_KEY, 0);
    }

    /**
     * 정답을 모두 맞추었지만 전화번호를 입력하지 않은 winner의 데이터를 주기적으로 삭제한다.
     */
    @Scheduled(cron = "* */5 * * * *") // 매 5분마다 실행
    public void deleteUnregisteredWinner() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(WINNER_KEY);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String ticketId = entry.getKey().toString();
            Long startTime = (Long) entry.getValue();
            Long diffTime = System.currentTimeMillis() - startTime;

            if (diffTime > 1000 * 60 * 3) {
                LocalDateTime convertedStartTime = SystemTimeConvertor.convertToLocalDateTime(startTime);
                LocalDateTime now = SystemTimeConvertor.convertToLocalDateTime(System.currentTimeMillis());

                deleteWinner(ticketId);
                Long count = decreaseCount();

                log.info("[DELETED] UUID : " + ticketId);
                log.info("[DELETED] 정답을 맞춘 시간: " + convertedStartTime);
                log.info("[DELETED] 현재 시간     : " + now);
                log.info("current count : " + count);
            }
        }
    }
}
