package ai.softeer.caecae.racinggame.service;

import ai.softeer.caecae.racinggame.domain.dto.request.PercentRequestDto;
import ai.softeer.caecae.racinggame.domain.dto.response.PercentResponseDto;
import ai.softeer.caecae.racinggame.repository.RacingGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RacingGameService {
    private final RacingGameRepository racingGameRepository;

    /**
     * 레이싱 게임 종료마다 사용자의 대략적인 랭킹을 반환하는 메서드
     *
     * @param req 레이싱 게임 사용자 기록
     * @return PercentResponseDto
     */
    public PercentResponseDto getRankingPercentage(PercentRequestDto req) {
        // 전체 기록을 100개의 등급으로 나누어, 첫 번째 값을 가져온 리스트
        // 등급 분포 개수 = max(기록 개수, 100)
        List<Double> calculatedGapRankedList = racingGameRepository.getCalculatedGapRankedList();

        double userRecordGap = Math.abs(req.distance() - 315);

        // 유저의 순위를 구하는 알고리즘 (lower bound)
        int rankSize = calculatedGapRankedList.size();
        int lo = 0, hi = rankSize; // 가능한 결과의 범위 = [lo, hi) = [0, rankSize) = rankSize개
        while (lo + 1 < hi) {
            int mid = (lo + hi) / 2;

            if (calculatedGapRankedList.get(mid) <= userRecordGap) lo = mid;
            else hi = mid;
        }

        // rankSize가 n이라면, n+1개의 구간으로 나타내기 위해 lo==0인 경우 check()룰 한번 더 수행
        if (rankSize != 0 && lo == 0 && calculatedGapRankedList.get(lo) > userRecordGap) {
            lo = -1;
        }
        lo += 1; // 가능한 결과의 범위를 [0,rankSize]로 변경

        // rankSize == 0인 경우엔 상위 1퍼로 반환
        double res = rankSize > 0 ? (double) lo / (rankSize) * 100 : 1;
        return PercentResponseDto.builder().percent(res).build();
    }


}

