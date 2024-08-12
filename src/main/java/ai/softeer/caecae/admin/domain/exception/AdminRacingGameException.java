package ai.softeer.caecae.admin.domain.exception;

import ai.softeer.caecae.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AdminRacingGameException extends RuntimeException {
    private final ErrorCode errorCode;

    public AdminRacingGameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
