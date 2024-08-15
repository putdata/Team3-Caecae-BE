package ai.softeer.caecae.findinggame.domain.exception;

import ai.softeer.caecae.global.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * FindingGame 도메인에서 에러를 핸들링하여 HttpResponse 를 반환하는 핸들러
 */
@Slf4j
@ControllerAdvice
public class FindingGameExceptionHandler {
    // FindingGameException 에 대한 에러 핸들링
    @ExceptionHandler(value = FindingGameException.class)
    public ResponseEntity<ErrorResponse> handleFindingGameException(FindingGameException findingGameException) {
        log.error(findingGameException.getMessage(), findingGameException);
        return ErrorResponse.of(findingGameException.getErrorCode());
    }
}
