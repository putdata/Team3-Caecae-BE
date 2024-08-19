package ai.softeer.caecae.shorturl.domain.exception;

import ai.softeer.caecae.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class UrlException extends RuntimeException {
    private final ErrorCode errorCode;

    public UrlException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
