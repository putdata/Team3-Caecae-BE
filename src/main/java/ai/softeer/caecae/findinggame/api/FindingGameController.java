package ai.softeer.caecae.findinggame.api;

import ai.softeer.caecae.findinggame.domain.dto.response.StartGameResponseDto;
import ai.softeer.caecae.findinggame.service.FindingGameService;
import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import ai.softeer.caecae.findinggame.domain.dto.response.FindingGameInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/finding")
@RequiredArgsConstructor
public class FindingGameController {
    private final FindingGameService findingGameService;

    /**
     * 숨은캐스퍼찾기 전체 게임 정보와 최근/다음 게임의 인덱스를 반환하는 api
     *
     * @return 전체 게임 정보, 최근/다음 게임의 인덱스
     */
    @GetMapping("/info")
    public ResponseEntity<SuccessResponse<FindingGameInfoResponseDto>> getFindingGameInfo() {
        FindingGameInfoResponseDto res = findingGameService.getFindingGameInfo();
        return SuccessResponse.of(SuccessCode.OK, res);
    }

    /**
     * 숨은캐스퍼찾기 게임 시작 가능 여부와 관련 정보를 반환하는 api
     *
     * @return 시작 가능 여부, 게임 관련 정보
     */
    @GetMapping("/start")
    public ResponseEntity<SuccessResponse<StartGameResponseDto>> startFindingGame() {
        return SuccessResponse.of(SuccessCode.OK, findingGameService.startFindingGame());
    }
}
