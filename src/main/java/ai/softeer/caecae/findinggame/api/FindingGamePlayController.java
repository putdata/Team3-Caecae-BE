package ai.softeer.caecae.findinggame.api;

import ai.softeer.caecae.findinggame.domain.dto.request.AnswerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.request.RegisterWinnerRequestDto;
import ai.softeer.caecae.findinggame.domain.dto.response.AnswerResponseDto;
import ai.softeer.caecae.findinggame.domain.dto.response.RegisterWinnerResponseDto;
import ai.softeer.caecae.findinggame.service.FindingGamePlayService;
import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/finding")
public class FindingGamePlayController {
    private final FindingGamePlayService findingGamePlayService;

    /**
     * 참여자가 게임의 정답을 보냄
     *
     * @param req
     * @return
     */
    @PostMapping("/answer")
    public ResponseEntity<SuccessResponse<AnswerResponseDto>> checkAnswer(@RequestBody AnswerRequestDto req) {
        return SuccessResponse.of(SuccessCode.OK, findingGamePlayService.checkAnswer(req));
    }

    /**
     * 선착순에 든 참여자가 전화번호를 입력
     *
     * @param req
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<RegisterWinnerResponseDto>> registWinner(@RequestBody RegisterWinnerRequestDto req) {
        return SuccessResponse.of(SuccessCode.OK, findingGamePlayService.registWinner(req));
    }
}
