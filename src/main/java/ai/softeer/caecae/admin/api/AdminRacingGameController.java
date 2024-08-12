package ai.softeer.caecae.admin.api;

import ai.softeer.caecae.admin.domain.dto.response.RacingGameWinnerResponseDto;
import ai.softeer.caecae.admin.service.AdminRacingGameService;
import ai.softeer.caecae.findinggame.service.FindingGameService;
import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import ai.softeer.caecae.racinggame.domain.dto.request.RegisterRacingGameInfoRequestDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RacingGameInfoResponseDto;
import ai.softeer.caecae.racinggame.domain.dto.response.RegisterRacingGameInfoResponseDto;
import ai.softeer.caecae.racinggame.service.RacingGameInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/racing")
@RequiredArgsConstructor
public class AdminRacingGameController {
    private final RacingGameInfoService racingGameService;
    private final FindingGameService findingGameService;
    private final AdminRacingGameService adminRacingGameService;

    /**
     * 관리자가 등록된 레이싱게임의 정보를 조회하는 로직
     *
     * @return 레이싱게임 정보
     */
    @GetMapping("/period")
    public ResponseEntity<SuccessResponse<RacingGameInfoResponseDto>> getRacingGameInfo() {
        RacingGameInfoResponseDto racingGameInfoDto = racingGameService.getRacingGameInfo();
        return SuccessResponse.of(SuccessCode.OK, racingGameInfoDto);
    }

    /**
     * 관리자가 레이싱게임의 시작시간, 종료시간, 당첨인원을 설정하는 api
     *
     * @param req 레이싱게임 정보
     */
    @PostMapping("/period")
    public ResponseEntity<SuccessResponse<RegisterRacingGameInfoResponseDto>> registerRacingGame(
            @RequestBody RegisterRacingGameInfoRequestDto req
    ) {
        RegisterRacingGameInfoResponseDto res = racingGameService.registerRacingGameInfo(req);
        return SuccessResponse.of(SuccessCode.RACING_GAME_CREATED, res);
    }

    /**
     * 관리자가 레이싱게임 종료 후, 당첨자를 뽑는 API
     *
     * @return 당첨자 리스트
     */
    @PostMapping("/winners")
    public ResponseEntity<SuccessResponse<List<RacingGameWinnerResponseDto>>> drawRacingGameWinner() {
        return SuccessResponse.of(SuccessCode.CREATED, adminRacingGameService.drawRacingGameWinner());
    }

    /**
     * 당첨자를 반환하는 API
     *
     * @return 당첨자 리스트
     */
    @GetMapping("/winners")
    public ResponseEntity<SuccessResponse<List<RacingGameWinnerResponseDto>>> getRacingGameWinner() {
        return SuccessResponse.of(SuccessCode.OK, adminRacingGameService.getRacingGameWinner());
    }
}