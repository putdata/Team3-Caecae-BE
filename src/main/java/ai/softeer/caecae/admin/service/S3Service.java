package ai.softeer.caecae.admin.service;

import ai.softeer.caecae.admin.domain.dto.response.S3ResponseDto;
import ai.softeer.caecae.admin.domain.exception.AdminFindingGameException;
import ai.softeer.caecae.global.enums.ErrorCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${spring.s3.bucket}")
    private String bucket;

    private static final List<String> DIRECTORY = List.of("answer", "question");


    /**
     * S3에 파일을 업로드 하는 서비스 로직
     *
     * @param file : S3에 업로드 할 멀티파트 파일
     * @return 파일 이름
     */
    @Transactional
    public S3ResponseDto uploadFile(MultipartFile file, String directory) {
        if (!DIRECTORY.contains(directory)) {
            throw new AdminFindingGameException(ErrorCode.S3_INVALID_DIRECTORY_NAME);
        }
        String fileName = createFileName(file.getOriginalFilename(), directory);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String imageUrl;

        // 파일 업로드
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
            imageUrl = amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new AdminFindingGameException(ErrorCode.INTERNAL_SERVER_ERROR);
            //TODO : 커스텀 에러 관리하기
        }
        log.info(imageUrl, " is successfully created in S3");
        return S3ResponseDto.builder().imageUrl(imageUrl).build();
    }

    // caecae + UUID 로 파일 이름 생성하기
    private String createFileName(String fileName, String directory) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(directory);
        stringBuilder.append("/caecae-");
        stringBuilder.append(UUID.randomUUID().toString().concat(getFileExtension(fileName)));
        return stringBuilder.toString();
    }

    // 파일 확장자 추출하기
    private String getFileExtension(String fileName) {
        log.info("fileName : {}", fileName.substring(fileName.lastIndexOf(".")));
        return fileName.substring(fileName.lastIndexOf("."));
    }
}