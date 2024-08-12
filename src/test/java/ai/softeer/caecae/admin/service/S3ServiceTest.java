package ai.softeer.caecae.admin.service;

import ai.softeer.caecae.admin.domain.dto.response.S3ResponseDto;
import ai.softeer.caecae.admin.domain.exception.AdminFindingGameException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
//@TestPropertySource(properties = {
//        "spring.s3.bucket=caecae"
//})
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    private MockMultipartFile mockMultipartFile;


    @BeforeEach
    void setUp() throws IOException {
        //given
        File file = ResourceUtils.getFile("classpath:hyundai.png");
        FileInputStream fileInputStream = new FileInputStream(file);
        mockMultipartFile = new MockMultipartFile(
                "file", // 파라미터 이름
                file.getName(), // 원본 파일 이름
                "image/png", // 파일 타입
                fileInputStream // 파일 데이터
        );
    }

    @Test
    @DisplayName("S3 이미지 업로드시 잘못된 디렉터리 네임 설정")
    void uploadFile_실패() throws IOException {

        //when & then
        assertThrows(AdminFindingGameException.class, () -> {
            s3Service.uploadFile(mockMultipartFile, "Answer");
        });
    }

    @Test
    @DisplayName("S3 이미지 업로드 성공")
    void uploadFile_성공() throws IOException {
        ReflectionTestUtils.setField(s3Service, "bucket", "test-bucket-name");

        // 성공할 때 반환되는 URL을 모킹
        String expectedUrl = "https://s3.ap-south-1.amazonaws.com/your-bucket-name/caecae-some-uuid.png";
        Mockito.when(amazonS3.getUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(new URL(expectedUrl));

        //when
        S3ResponseDto response = s3Service.uploadFile(mockMultipartFile, "answer");


        //then
        Mockito.verify(amazonS3).putObject(Mockito.any(PutObjectRequest.class));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedUrl, response.imageUrl());
    }
}

///

