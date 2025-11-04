package com.tochookpi.tochookpi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, "GLOBAL-001", "사용자의 잘못된 입력"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL-002", "잘못된 요청"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLOBAL-003", "인증 필요"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL-004", "접근 권한 필요"),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL-005", "요청 데이터 없음"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-006", "서버 문제 발생"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GLOBAL-007", "서비스 사용 불가"),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "GLOBAL-008", "필수 파라미터가 없습니다"),

    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-001", "Access Token 만료"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "Refresh Token 만료"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "잘못된 토큰"),
    TOKEN_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "AUTH-004", "토큰 미제공"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-005", "접근 권한 없음"),
    SOCIAL_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-006", "소셜 로그인 인증 실패"),
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "AUTH-007", "인증 코드 유효 하지 않음"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-008", "이메일 또는 비밀번호가 올바르지 않음"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없음"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "USER-002", "이미 존재하는 사용자"),
    INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "USER-003", "비밀번호가 일치하지 않음"),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "USER-004", "사용자 접근이 거부됨"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "USER-005", "이메일 형식이 올바르지 않음"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER-006", "비밀번호 형식이 올바르지 않음"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER-007", "이미 사용 중인 이메일"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER-008", "이미 사용 중인 닉네임"),
    INACTIVE_ACCOUNT(HttpStatus.FORBIDDEN, "USER-009", "비활성화된 계정"),
    BANNED_ACCOUNT(HttpStatus.FORBIDDEN, "USER-010", "정지된 계정"),
    FAIL_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "USER-011", "이미지 업로드에 실패했습니다."),
    FAIL_MODIFY_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "USER-012", "유저 정보 수정에 실패했습니다."),
    FAIL_MODIFY_USER_SETTING(HttpStatus.INTERNAL_SERVER_ERROR, "USER-013", "기본 설정 수정에 실패했습니다."),

    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-001", "모임를 찾을 수 없음"),
    MEETING_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MEETING-002", "모임 접근 권한이 없음"),
    MEETING_ALREADY_STARTED(HttpStatus.CONFLICT, "MEETING-003", "모임 일정이 이미 시작됨"),
    MEETING_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "MEETING-004", "모임 일정이 이미 종료됨"),
    MEETING_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "MEETING-005", "이미 참여한 모임"),
    MEETING_NOT_JOINED(HttpStatus.BAD_REQUEST, "MEETING-006", "참여한 모임이 아님"),
    MEETING_ORGANIZER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "MEETING-007", "주최자는 모임을 나갈 수 없음"),
    MEETING_FAIL_SAVE_MEETING(HttpStatus.INTERNAL_SERVER_ERROR, "MEETING-008", "모임 저장 실패"),
    MEETING_ONLY_ORGANIZER(HttpStatus.FORBIDDEN, "MEETING-009", "해당 기능은 주최자만 사용 가능"),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-001", "모임 후기를 찾을 수 없습니다."),
    REVIEW_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "REVIEW-002", "이미 삭제된 후기입니다."),
    REVIEW_ONLY_WRITER(HttpStatus.FORBIDDEN, "REVIEW-003", "해당 기능은 작성자만 후기 작성자만 이용 가능"),
    REVIEW_FAIL_SAVE_REVIEW(HttpStatus.FORBIDDEN, "REVIEW-004", "후기 저장 실패"),
    REVIEW_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-005", "모임 후기 파일을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
