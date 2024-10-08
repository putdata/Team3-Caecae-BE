package ai.softeer.caecae.global.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Http 요청에 대한 에러 응답과 관련있는 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode implements BaseCode {
    //TODO : 코드 문서화 및 에러코드 음수화,
    /**
     * 1000 : 틀린그림찾기
     */
    NOT_FOUND(-1000, "잘못된 요청입니다.", HttpStatus.NOT_FOUND),

    /**
     * 2xxx : 레이싱게임
     */
    NEED_AUTHENICATE(1000, "권한이 필요한 요청입니다,", HttpStatus.UNAUTHORIZED),
    RACING_GAME_NOT_FOUND(2000, "등록된 레이싱게임이 없습니다.", HttpStatus.NOT_FOUND),
    SELECTION_ALREADY_SELECTED(-2000, "이미 선택된 커스텀 옵션이 존재합니다.", HttpStatus.BAD_REQUEST),
    RACING_GAME_NOT_AVAILABLE(-1000, "현재 레이싱게임을 이용할 수 없습니다.", HttpStatus.UNAUTHORIZED),

    /**
     * 3xxx : 유저
     */
    USER_NOT_FOUND(-3000, "해당 휴대폰 번호로 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /**
     * 4xxx : 어드민 - 틀린그림찾기
     */

    FINDING_GAME_OF_DAY_NOT_FOUND(-4000, "해당 날짜에 등록된 틀린그림찾기 게임이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    CURRENT_FINDING_GAME_NOT_FOUND(-4000, "현재 진행중인 숨은캐스퍼찾기 게임이 없습니다", HttpStatus.NOT_FOUND),
    INVALID_FINDING_GAME_ANSWER(-4000, "숨은캐스퍼찾기 게임의 정답이 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FINDING_GAME_HINT(-4000, "숨은캐스퍼찾기 게임의 힌트를 적절하게 요청하지 못했습니다.", HttpStatus.BAD_REQUEST),
    S3_IMAGE_UPLOAD_FAIL(-4001, "S3 이미지 업로드에 실패하였습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    S3_INVALID_DIRECTORY_NAME(-4001, "S3 버킷 디렉토리명이 잘못되었습니다.", HttpStatus.BAD_REQUEST),


    /**
     * 5xxx : 어드민 - 레이싱게임
     */

    /**
     * 9xxx : 기타 에러
     */
    INTERNAL_SERVER_ERROR(-9000, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(-9000, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

    private final int responseCode;
    private final String message;
    private final HttpStatus httpStatus;

}
