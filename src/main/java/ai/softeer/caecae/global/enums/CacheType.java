package ai.softeer.caecae.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    FINDING_GAME_KEY("FindingGameInfo", 10, 1000),
    RECENT_FINDING_GAME("RecentFindingGame", 20, 1000),
    ALL_FINDING_GAME_ANSWERS_BY_GAME_ID("AllFindingGameAnswersByGameId", 20, 1000),
    ALL_FINDING_GAME("AllFindingGame", 20, 1000),
    CALCULATED_GAP_RANKED_LIST("CalculatedGapRankedList", 5 * 60, 1000);

    /**
     * cacheName : 캐시 이름
     * expiredAfterWrite : 캐시 만료 시간(TTL, Second)
     * maximumSize : 캐시 최대 사이즈(최대 엔트리 수)
     */

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}