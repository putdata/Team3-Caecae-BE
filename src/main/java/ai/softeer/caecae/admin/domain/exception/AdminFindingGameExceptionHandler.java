package ai.softeer.caecae.admin.domain.exception;

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
public class AdminFindingGameExceptionHandler {
    // FindingGameException 에 대한 에러 핸들링
    @ExceptionHandler(value = AdminFindingGameException.class)
    public ResponseEntity<ErrorResponse> handleFindingGameException(AdminFindingGameException adminException) {
        log.error(adminException.getMessage(), adminException);
        return ErrorResponse.of(adminException.getErrorCode());
    }
}
