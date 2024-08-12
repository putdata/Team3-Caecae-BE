package ai.softeer.caecae.admin.api;

import ai.softeer.caecae.admin.domain.dto.response.S3ResponseDto;
import ai.softeer.caecae.admin.service.S3Service;
import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/s3")
public class S3Controller {
    // TBD : Admin의 컨트롤러들, 이미지 업로드 등의 기능을 일반 사용자가 이용 못하게 막아야 하지 않는가?
    private final S3Service s3Service;

    @PostMapping("")
    public ResponseEntity<SuccessResponse<S3ResponseDto>> upload(
            @RequestParam("file") MultipartFile file,
            String directory
    ) {
        S3ResponseDto res = s3Service.uploadFile(file, directory);
        return SuccessResponse.of(SuccessCode.CREATED, res);
    }
}
