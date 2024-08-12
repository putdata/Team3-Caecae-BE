package ai.softeer.caecae.admin.domain.dto.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

// S3에 이미지를 업로드 할 때 사용하는 요청 객체
@Builder
public record S3RequestDto(
        MultipartFile file
) {
}
