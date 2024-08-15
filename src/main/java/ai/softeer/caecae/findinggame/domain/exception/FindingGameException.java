package ai.softeer.caecae.findinggame.domain.exception;

import ai.softeer.caecae.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class FindingGameException extends RuntimeException {
    private final ErrorCode errorCode;

    public FindingGameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
