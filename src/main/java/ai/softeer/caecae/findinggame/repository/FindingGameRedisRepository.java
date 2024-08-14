package ai.softeer.caecae.findinggame.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FindingGameRedisRepository {
    private final static String TICKET_KEY = "ticket";
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
}
