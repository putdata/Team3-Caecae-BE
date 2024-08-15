package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.request.AnswerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.PositionDto;
import ai.softeer.caecae.findinggame.domain.dto.request.RegisterWinnerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.response.AnswerResponseDto;
import ai.softeer.caecae.findinggame.domain.dto.response.CorrectAnswerDto;
import ai.softeer.caecae.findinggame.domain.dto.response.RegisterWinnerResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameAnswer;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.domain.exception.FindingGameException;
import ai.softeer.caecae.findinggame.repository.FindingGameAnswerDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.global.enums.ErrorCode;
import ai.softeer.caecae.user.domain.entity.User;
import ai.softeer.caecae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingGamePlayService {
    private final FindingGameRedisRepository findingGameRedisRepository;
    private final FindingGameDbRepository findingGameDbRepository;
    private final FindingGameWinnerRepository findingGameWinnerRepository;
    private final FindingGameAnswerDbRepository findingGameAnswerDbRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    private final int MAX_ANSWER_COUNT = 2;
    private final double ANSWER_RADIUS = 0.1;
    private final long CONSTRAINT_TIME = 1000L * 60 * 3;


    /**
     * 사용자가 보낸 정답을 채점하고 모두 맞으면 선착순 인원에 들었는지 확인하는 서비스 로직
     *
     * @param req
     * @return
     */
    public AnswerResponseDto checkAnswer(AnswerRequestDto req) {
        // TODO : req의 position 0~1 validation

        // 현재 진행중인 숨은캐스퍼찾기 게임 반환
        List<FindingGame> findingGames = findingGameDbRepository.findAllOrderByStartTimeCacheable();
        FindingGame currentFindingGame = getCachedCurrentFindingGame(findingGames).orElseThrow(
                () -> new FindingGameException(ErrorCode.CURRENT_FINDING_GAME_NOT_FOUND)
        );

        // 숨은캐스퍼찾기 정답 리스트 deep copy
        List<FindingGameAnswer> correctList = new ArrayList<>(getCachedCurrentFindingGameAnswer(currentFindingGame.getId()));
        if (correctList.size() != MAX_ANSWER_COUNT) {
            throw new FindingGameException(ErrorCode.INVALID_FINDING_GAME_ANSWER);
        }

        // 정답 판단
        List<PositionDto> answerList = req.answerList(); // 사용자가 보낸 리스트
        List<CorrectAnswerDto> correctAnswerList = new ArrayList<>(); // 사용자에게 보낼 채점한 리스트
        int correctAnswerCount = 0; // 사용자가 맞춘 정답의 개수
        for (PositionDto answer : answerList) {
            double positionX = answer.positionX();
            double positionY = answer.positionY();
            log.info("user answer : posX: {}, posY: {}", positionX, positionY);
            for (FindingGameAnswer correct : correctList) {
                double correctX = correct.getPositionX();
                double correctY = correct.getPositionY();

                // 점과 점 사이의 거리
                double diff = Math.sqrt((positionX - correctX) * (positionX - correctX) + (positionY - correctY) * (positionY - correctY));

                if (diff <= ANSWER_RADIUS) { // TODO: 정답 범위 0.1에서 조정 필요
                    correctAnswerCount++;
//                  정답을 1개만 맞추고 1개를 지운 경우, 아래의 코드 때문에, getCachedCurrentFindingGameAnswer 에서 가져오는 정답이 2개에서 1개로 바뀌는 버그
                    correctList.remove(correct);
                    correctAnswerList.add(
                            CorrectAnswerDto.builder()
                                    .positionX(positionX)
                                    .positionY(positionY)
                                    .descriptionImageUrl(correct.getDescriptionImageUrl())
                                    .title(correct.getTitle())
                                    .content(correct.getContent())
                                    .build()
                    );
                    break;
                }
            }
        }

        // 정답을 2개 모두 맞추지 못했거나, 선착순 315명 안에 들지 못한 경우
        if (correctAnswerCount != MAX_ANSWER_COUNT ||
                findingGameRedisRepository.increaseCount() > currentFindingGame.getNumberOfWinners()) {
            // 정답을 모두 맞추었다면 315명안에 들지 못했으므로 카운트를 감소시켜 315로 유지
            if (correctAnswerCount == MAX_ANSWER_COUNT) {
                findingGameRedisRepository.decreaseCount();
                log.info("정답을 모두 맞추었지만 선착순 인원 내에 들지 못하였음.");
            }

            return AnswerResponseDto.builder()
                    .correctAnswerList(correctAnswerList)
                    .ticketId("")
                    .startTime(-1L)
                    .build();
        }

        // TicketId 생성
        String ticketId = UUID.randomUUID().toString();
        Long startTime = findingGameRedisRepository.addWinner(ticketId);
        return AnswerResponseDto.builder()
                .correctAnswerList(correctAnswerList)
                .ticketId(ticketId)
                .startTime(startTime)
                .build();
    }

    /**
     * 선착순에 든 사용자가 전화번호를 입력했을 때 처리하는 서비스 로직
     *
     * @param req
     * @return 정답 리스트와 선착순 참가자 등록 정보
     */
    @Transactional
    public RegisterWinnerResponseDto registWinner(RegisterWinnerRequestDto req) {
        String ticketId = req.ticketId();
        Long startTime = findingGameRedisRepository.getWinnerStartTime(ticketId);
        if (startTime == null) return RegisterWinnerResponseDto.builder().success(false).build(); // 목록에 없으면
        log.info("사용자 시작시간: {}", startTime);
        Long endTime = System.currentTimeMillis() - 1000L; // 1초 지연 허용
        if (endTime - startTime > CONSTRAINT_TIME) { // 3분 초과 - 실패 및 당첨자 제외
            findingGameRedisRepository.decreaseCount();
            findingGameRedisRepository.deleteWinner(ticketId);
            return RegisterWinnerResponseDto.builder()
                    .success(false)
                    .build();
        }
        Integer gameId = 1;
        String phone = req.phone();
        findingGameDbRepository.findById(gameId);
        FindingGameWinner winner = FindingGameWinner.builder()
                .user(userRepository.findByPhone(phone).orElseGet(() -> userRepository.save(
                        User.builder()
                                .phone(phone)
                                .build()
                )))
                .findingGame(findingGameDbRepository.findById(gameId).get())
                .build();
        findingGameWinnerRepository.save(winner);
        findingGameRedisRepository.deleteWinner(ticketId);
        return RegisterWinnerResponseDto.builder()
                .success(true)
                .build();
    }

    /**
     * 캐싱된 전체 숨은그림찾기 게임을 이용하여 현재 진행중인 게임 찾기
     *
     * @param findingGames 전체 숨은그림찾기 게임
     * @return 현재 진행중인 게임 엔티티
     */
    private Optional<FindingGame> getCachedCurrentFindingGame(List<FindingGame> findingGames) {
        LocalDateTime now = LocalDateTime.now(clock);
        for (FindingGame findingGame : findingGames) {
            if (!findingGame.getStartTime().isAfter(now) && !findingGame.getEndTime().isBefore(now)) {
                return Optional.of(findingGame);
            }
        }
        return Optional.empty();
    }

    /**
     * 숨은캐스퍼찾기 gameId를 이용하여 캐싱된 정답 정보 가져오기
     *
     * @param findingGameId 숨은캐스퍼찾기 gameId
     * @return 정답 정보
     */
    private List<FindingGameAnswer> getCachedCurrentFindingGameAnswer(int findingGameId) {
        return findingGameAnswerDbRepository.findAllByFindingGame_Id(findingGameId);
    }
}
