package ai.softeer.caecae.findinggame.service;

import ai.softeer.caecae.findinggame.domain.dto.request.AnswerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.request.CoordDto;
import ai.softeer.caecae.findinggame.domain.dto.request.RegisterWinnerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.response.AnswerResponseDto;
import ai.softeer.caecae.findinggame.domain.dto.response.RegisterWinnerResponseDto;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameRedisRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.user.domain.entity.User;
import ai.softeer.caecae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingGamePlayService {
    private final FindingGameRedisRepository findingGameRedisRepository;
    private final FindingGameDbRepository findingGameDbRepository;
    private final FindingGameWinnerRepository findingGameWinnerRepository;
    private final UserRepository userRepository;

    private final int MAX_ANSWER_COUNT = 2;
    private final int ANSWER_RANGE = 900;
    private final long CONSTRAINT_TIME = 1000L * 60 * 3;

    /**
     * 사용자가 보낸 정답을 채점하고 모두 맞으면 선착순 인원에 들었는지 확인하는 서비스 로직
     *
     * @param req
     * @return
     */
    public AnswerResponseDto checkAnswer(AnswerRequestDto req) {
        // 정답 판단
        List<CoordDto> answerList = req.answerList(); // 사용자가 보낸 리스트
        List<CoordDto> correctList = new ArrayList<>(); // 정답 리스트
        correctList.add(new CoordDto(10, 10)); // 임시 정답 데이터
        correctList.add(new CoordDto(30, 30)); // TODO: 정답 정보 가져와야함
        List<CoordDto> correctAnswerList = new ArrayList<>(); // 사용자에게 보낼 채점한 리스트
        int count = 0;
        for (CoordDto answer : answerList) {
            int x = answer.coordX(), y = answer.coordY();
            log.info("X: {}, Y: {}", x, y);
            for (CoordDto correct : correctList) {
                int cx = correct.coordX(), cy = correct.coordY();
                int diff = (x - cx) * (x - cx) + (y - cy) * (y - cy); // 점과 점 사이의 거리 제곱 공식
                if (diff <= ANSWER_RANGE) { // TODO: 정답 범위 _ 수정 요망
                    correctAnswerList.add(correct);
                    count++;
                    correctList.remove(correct);
                    break;
                }
            }
        }

        // 정답 개수가 0이거나 1 || 남은 선착순 자리 체크
        if (count != MAX_ANSWER_COUNT || findingGameRedisRepository.increaseCount() > 315L) { // TODO: 오늘 선착순 인원 정보 가져와야함
            if (count == MAX_ANSWER_COUNT) findingGameRedisRepository.decreaseCount();
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
}
