package ai.softeer.caecae.admin.api;

import ai.softeer.caecae.admin.domain.dto.request.FindingGameDailyAnswerRequestDto;
import ai.softeer.caecae.admin.domain.dto.response.FindingGameDailyAnswerResponseDto;
import ai.softeer.caecae.admin.service.AdminFindingGameService;
import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import ai.softeer.caecae.racinggame.domain.dto.request.RegisterFindingGamePeriodRequestDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RegisterFindingGamePeriodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/finding")
@RequiredArgsConstructor
public class AdminFindingGameController {
    private final AdminFindingGameService adminFindingGameService;

    /**
     * 어드민이 숨은캐스퍼찾기 게임 기간을 등록하는 api
     *
     * @param req 게임 시작 날짜
     * @return 등록된 게임 시작 날짜, 종료 날짜(+6일)
     */
    @PostMapping("/period")
    public ResponseEntity<SuccessResponse<RegisterFindingGamePeriodResponseDto>>
    registerFindingGamePeriod(@RequestBody RegisterFindingGamePeriodRequestDto req) {
        RegisterFindingGamePeriodResponseDto res = adminFindingGameService.registerFindingGamePeriod(req);
        return SuccessResponse.of(SuccessCode.CREATED, res);
    }

    /**
     * 어드민이 숨은캐스퍼찾기 날짜별 정답을 등록하는 api
     *
     * @param req
     * @return
     */
    @PostMapping("/answer")
    public ResponseEntity<SuccessResponse<FindingGameDailyAnswerResponseDto>>
    saveFindingGameDailyInfo(@RequestBody FindingGameDailyAnswerRequestDto req) {
        FindingGameDailyAnswerResponseDto res = adminFindingGameService.saveFindingGameDailyAnswer(req);
        return SuccessResponse.of(SuccessCode.OK, res);
    }
}
