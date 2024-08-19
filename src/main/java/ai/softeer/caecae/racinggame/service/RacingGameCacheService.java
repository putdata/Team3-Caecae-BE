package ai.softeer.caecae.racinggame.service;

import ai.softeer.caecae.racinggame.repository.RacingGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RacingGameCacheService {
    private final RacingGameRepository racingGameRepository;

    @Scheduled(cron = "0 */5 * * * *") // 매 5분마다 실행
    public void refreshCalculatedGapRankedListCache() {
        clearCalculatedGapRankedListCache();
        int size = racingGameRepository.getCalculatedGapRankedList().size();
        log.info("레이싱게임 랭킹 테이블 갱신 : rankSize: " + size);
    }

    @CacheEvict(cacheNames = "CalculatedGapRankedList")
    public void clearCalculatedGapRankedListCache() {
    }
}
