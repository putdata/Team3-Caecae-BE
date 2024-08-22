package ai.softeer.caecae.racinggame.repository;

import ai.softeer.caecae.racinggame.domain.entity.RacingGameInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
//@RequiredArgsConstructor
public class RacingGameInfoRepository {
    // 레디스에 저장될 레이싱게임 정보의 키
    private final static String KEY = "racingGameInfo";

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @Qualifier("redisReadOnlyTemplate")
    private final RedisTemplate<String, Object> redisReadonlyTemplate;

    public RacingGameInfoRepository(
            @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
            @Qualifier("redisReadOnlyTemplate") RedisTemplate<String, Object> redisReadonlyTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisReadonlyTemplate = redisReadonlyTemplate;
    }

    // 레디스에 저장하는 로직
    public RacingGameInfo save(RacingGameInfo gameInfo) {
        redisTemplate.opsForValue().set(KEY, gameInfo);
        return gameInfo;
    }

    // 레디스에 저장된 객체를 가져오는 로직
    public RacingGameInfo get() {
        return (RacingGameInfo) redisReadonlyTemplate.opsForValue().get(KEY);
    }
}
