package ai.softeer.caecae.admin.domain.dto.response;

import lombok.Builder;

// S3에 이미지를 업로드 한 후 받는 응답 객체
@Builder
public record S3ResponseDto(
        String imageUrl
) {
}
