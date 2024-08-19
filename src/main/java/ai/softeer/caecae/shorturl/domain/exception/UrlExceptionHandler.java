package ai.softeer.caecae.shorturl.domain.exception;

import ai.softeer.caecae.global.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * url 도메인에서 에러를 핸들링하여 HttpResponse 를 반환하는 핸들러
 */
@Slf4j
@ControllerAdvice
public class UrlExceptionHandler {
    // urlException 에 대한 에러 핸들링
    @ExceptionHandler(value = UrlException.class)
    public ResponseEntity<ErrorResponse> handleUrlException(UrlException urlException) {
        log.error(urlException.getMessage(), urlException);
        return ErrorResponse.of(urlException.getErrorCode());
    }
}
