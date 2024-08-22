package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.entity.FindingGameRealWinner;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.racinggame.repository.RacingGameRepository;
import ai.softeer.caecae.user.domain.entity.User;
import ai.softeer.caecae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingGameScheduleService {
    private final FindingGameRedisRepository findingGameRedisRepository;
    private final FindingGameDbRepository findingGameDbRepository;
    private final FindingGameWinnerRepository findingGameWinnerRepository;
    private final UserRepository userRepository;

    private final int TRANSACTION_COUNT = 100;

    @Transactional
    @Scheduled(cron = "*/2 * * * * *")
    public void insertWinnerToDatabaseScheduler() {
        int completed = 0;
        List<FindingGameWinner> winners = new ArrayList<>();
        for (int i = 0; i < TRANSACTION_COUNT; i++) {
            FindingGameRealWinner realWinner = findingGameRedisRepository.getFrontRealWinner();
            if (realWinner == null) break;
            Integer gameId = realWinner.getGameId();
            String phone = realWinner.getPhone();
            FindingGameWinner winner = FindingGameWinner.builder()
                    .user(userRepository.findByPhone(phone).orElseGet(() -> userRepository.save(
                            User.builder()
                                    .phone(phone)
                                    .build()
                    )))
                    .findingGame(findingGameDbRepository.findById(gameId).get())
                    .build();
            winners.add(winner);
            findingGameRedisRepository.popRealWinner();
            completed++;
        }
        findingGameWinnerRepository.saveAll(winners);
        log.info("선착순 인원 스케쥴링 처리 완료 - {} 건", completed);
    }
}
