package ai.softeer.caecae.admin.service;

import ai.softeer.caecae.admin.domain.dto.FindingGameAnswerDto;
import ai.softeer.caecae.admin.domain.dto.FindingGameWinnerDto;
import ai.softeer.caecae.admin.domain.dto.request.FindingGameDailyAnswerRequestDto;
import ai.softeer.caecae.admin.domain.dto.response.FindingGameDailyAnswerResponseDto;
import ai.softeer.caecae.admin.domain.dto.response.FindingGameWinnerResponseDto;
import ai.softeer.caecae.admin.domain.exception.AdminFindingGameException;
import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameAnswer;
import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import ai.softeer.caecae.findinggame.domain.enums.AnswerType;
import ai.softeer.caecae.findinggame.repository.FindingGameAnswerDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameDbRepository;
import ai.softeer.caecae.findinggame.repository.FindingGameWinnerRepository;
import ai.softeer.caecae.global.enums.ErrorCode;
import ai.softeer.caecae.racinggame.domain.dto.request.RegisterFindingGamePeriodRequestDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RegisterFindingGamePeriodResponseDto;
import ai.softeer.caecae.user.domain.entity.User;
import ai.softeer.caecae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFindingGameService {
    private final FindingGameDbRepository findingGameDbRepository;
    private final FindingGameAnswerDbRepository findingGameAnswerDbRepository;
    private final FindingGameWinnerRepository findingGameWinnerRepository;
    private final UserRepository userRepository;


    /**
     * 숨은캐스퍼찾기 게임 날짜별 정답 정보, 게임시작시간, 종료시간, 당첨인원수를 업데이트하는 로직
     *
     * @param req
     * @return
     */
    @Transactional
    public FindingGameDailyAnswerResponseDto saveFindingGameDailyAnswer(
            FindingGameDailyAnswerRequestDto req
    ) {

        // TODO : req.day 가 1~7인지 검증
        FindingGame findingGame = findingGameDbRepository
                // FindingGame 테이블의 1~7번째 열에 데이터가 들어간다고 가정하고, dayOfEvent 를 id로 활용하여 조회함
                // 좋은 방식은 아닌 것 같아서, 추후 어떻게 할지 논의 하면 좋겠음.
                .findById(req.dayOfEvent()).orElseThrow(() -> new AdminFindingGameException(ErrorCode.FINDING_GAME_OF_DAY_NOT_FOUND));

        // fingingGame의 시작시간, 종료시간, 당첨자수, 정답타입, 정답이미지 새로운 정보로 업데이트
        findingGame.updateFindingGamePeriod(
                findingGame.getStartTime().with(req.startTime()),
                findingGame.getEndTime().with(req.endTime()),
                req.numberOfWinner(),
                req.answerType(),
                req.questionImageUrl()
        );
        FindingGame savedFindingGame = findingGameDbRepository.save(findingGame);

        // findingGame의 2개의 정담(findingGameAnswer) 정보를 업데이트
        List<FindingGameAnswer> findingGameAnswerList = findingGameAnswerDbRepository
                .findAllByFindingGame_Id(req.dayOfEvent());

        // findingGameAnswer 정보가 존재하지 않는다면 초기화
        if (findingGameAnswerList.isEmpty()) {
            findingGameAnswerList = initFindingGameAnswer(findingGame);
        }

        // 2개의 정답 정보를 request에 들어온 대로 업데이트
        for (int idx = 0; idx < 2; idx++) {
            FindingGameAnswerDto findingGameAnswerDto = req.answerInfoList().get(idx);
            FindingGameAnswer findingGameAnswer = findingGameAnswerList.get(idx);

            findingGameAnswer.updateFindingGame(
                    findingGameAnswerDto.positionX(),
                    findingGameAnswerDto.positionY(),
                    findingGameAnswerDto.descriptionImageUrl(),
                    findingGameAnswerDto.title(),
                    findingGameAnswerDto.content()
            );
        }

        findingGameAnswerDbRepository.saveAll(findingGameAnswerList);

        return FindingGameDailyAnswerResponseDto.builder()
                .dayOfEvent(req.dayOfEvent())
                .numberOfWinner(savedFindingGame.getNumberOfWinners())
                .startTime(savedFindingGame.getStartTime())
                .endTime(savedFindingGame.getEndTime())
                .answerType(savedFindingGame.getAnswerType())
                .questionImageUrl(savedFindingGame.getQuestionImageUrl())
                .answerInfoList(req.answerInfoList())
                .build();


    }

    private List<FindingGameAnswer> initFindingGameAnswer(FindingGame findingGame) {
        List<FindingGameAnswer> findingGameAnswerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            findingGameAnswerList.add(FindingGameAnswer.builder()
                    .positionX(-1)
                    .positionY(-1)
                    .descriptionImageUrl("no-image")
                    .title("no-title")
                    .content("no-content")
                    .findingGame(findingGame)
                    .build());
        }
        return findingGameAnswerList;
    }

    /**
     * 어드민이 숨은캐스퍼찾기 게임 기간을 등록하는 로직
     *
     * @param req 게임 시작 날짜
     * @return 게임 시작 날짜, 종료 날짜(+6일)
     */
    @Transactional
    public RegisterFindingGamePeriodResponseDto registerFindingGamePeriod(RegisterFindingGamePeriodRequestDto req) {
        List<FindingGame> findingGames = findingGameDbRepository.findAll();
        // 등록된 게임 정보가 없으면 생성하기
        if (findingGames.isEmpty()) {
            findingGames = initFindingGames();
        }

        // 게임 정보 기간 업데이트
        LocalDate date = req.startDate();
        for (FindingGame findingGame : findingGames) {
            findingGame.updateFindingGamePeriod(
                    date.atTime(15, 15),
                    date.plusDays(1).atTime(14, 15)
            );
            date = date.plusDays(1);
        }


        findingGameDbRepository.saveAll(findingGames);

        return RegisterFindingGamePeriodResponseDto.builder()
                .startDate(req.startDate())
                .endDate(req.startDate().plusDays(6))
                .build();
    }

    // 7개의 숨은캐스퍼찾기 게임 정보 객체 초기화
    private List<FindingGame> initFindingGames() {
        List<FindingGame> findingGames = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            findingGames.add(
                    FindingGame.builder()
                            .id(day + 1) // PK를 1~7로 고정
                            .questionImageUrl("no-image")
                            .numberOfWinners(315)
                            .answerType(AnswerType.UNSELECTED)
                            .build()
            );
        }
        return findingGames;
    }

    public FindingGameWinnerResponseDto getFindingGameWinner() {
        List<FindingGameWinner> findingGameWinners = findingGameWinnerRepository.findAll();
        List<FindingGameWinnerDto> findingGameWinnerDtos = new ArrayList<>();

        for (FindingGameWinner findingGameWinner : findingGameWinners) {
            Integer userId = findingGameWinner.getUser().getId();
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new AdminFindingGameException(ErrorCode.USER_NOT_FOUND)
            );
            findingGameWinnerDtos.add(
                    FindingGameWinnerDto.builder()
                            .day(findingGameWinner.getFindingGame().getId())
                            .phone(user.getPhone())
                            .build()
            );

        }

        return FindingGameWinnerResponseDto.builder()
                .winners(findingGameWinnerDtos)
                .build();
    }

}
