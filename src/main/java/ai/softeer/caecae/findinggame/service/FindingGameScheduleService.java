package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.entity.FindingGameRealWinner;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.user.domain.entity.User;
import ai.softeer.caecae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                insertWinner(gameId, phone);
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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected void insertWinner(Integer gameId, String phone) {
        Integer userId = userRepository.findByPhone(phone).orElseGet(() -> userRepository.save(
                User.builder()
                        .phone(phone)
                        .build()
        )).getId();
        findingGameWinnerRepository.insertWinner(userId, gameId);
    }
}
