package ai.softeer.caecae.shorturl.service;

import ai.softeer.caecae.global.enums.ErrorCode;
import ai.softeer.caecae.global.utils.Encrypt;
import ai.softeer.caecae.shorturl.domain.entity.ShortUrlEntity;
import ai.softeer.caecae.shorturl.domain.exception.UrlException;
import ai.softeer.caecae.shorturl.domain.request.ShareUrlRequestDto;
import ai.softeer.caecae.shorturl.domain.response.ShareUrlResponseDto;
import ai.softeer.caecae.shorturl.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;

    private final int SHORT_URL_LENGTH = 7;

    /**
     * 레이싱게임 순위 단축 URL로 공유하기 기능
     *
     * @param req
     * @return 단축 URL
     */
    @Transactional
    public ShareUrlResponseDto shareUrl(ShareUrlRequestDto req) {
        double distance = Math.round(req.distance() * 1e6) / 1e6; // 소수점 6자리까지만 반영
        double percentage = Math.round(req.percentage() * 1e2) / 1e2;
        if (distance > 400.0 || percentage > 100) throw new UrlException(ErrorCode.BAD_REQUEST);

        String longUrl = "/api/url/preview?distance=" + distance + "&percentage=" + percentage;

        Optional<ShortUrlEntity> checkShortUrlEntity = shortUrlRepository.findByLongUrl(longUrl);
        if (checkShortUrlEntity.isPresent()) { // longUrl에 대해 이미 존재하는 경우
            return ShareUrlResponseDto.builder()
                    .shortUrl(checkShortUrlEntity.get().getShortUrl())
                    .build();
        }

        String shortUrl = new String(longUrl);
        int loopCnt = 0; // 무한 루프 방지를 위한 변수
        do {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                char[] encryted = Encrypt.SHA256(shortUrl).toCharArray();
                // SHA256의 결과는 소문자 or 숫자. 단축 URL에 a-zA-Z0-9 모두 쓰기 위해 일부를 대문자로 변환
                for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                    if ('a' <= encryted[i] && encryted[i] <= 'z' && encryted[i + SHORT_URL_LENGTH] % 2 == 1) {
                        stringBuilder.append((char) (encryted[i] - 32));
                    } else stringBuilder.append(encryted[i]);
                }
                shortUrl = stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new UrlException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            if (++loopCnt > 10) throw new UrlException(ErrorCode.INTERNAL_SERVER_ERROR);
        } while (!shortUrlRepository.findByShortUrl(shortUrl).isEmpty()); // shortUrl 중복이 안될 때 까지
        ShortUrlEntity shortUrlEntity = ShortUrlEntity.builder()
                .shortUrl(shortUrl)
                .longUrl(longUrl)
                .build();
        shortUrlRepository.save(shortUrlEntity);
        return ShareUrlResponseDto.builder()
                .shortUrl(shortUrl)
                .build();
    }

    /**
     * 단축 URL 접속 시 원래 주소로 리다렉션
     *
     * @param shortUrl
     */
    public ResponseEntity<?> shortUrlToLongUrl(String shortUrl) {
        Optional<ShortUrlEntity> shortUrlEntity = shortUrlRepository.findByShortUrl(shortUrl);
        return shortUrlEntity.map(urlEntity -> ResponseEntity.status(301)
                        .header("Location", urlEntity.getLongUrl())
                        .build())
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }

    /**
     * 동적인 데이터를 포함하는 카카오톡 미리보기를 위해 작성된 서비스 로직
     *
     * @param req
     * @return
     */
    public ResponseEntity<?> previewUrl(ShareUrlRequestDto req) {
        return ResponseEntity.status(200)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(
                        "<html>" +
                            "<head>" +
                                "<meta charset=\"utf-8\">" +
                                "<meta property=\"og:type\" content=\"website\" />" +
                                "<meta property=\"og:title\" content=\"레이싱 게임\">" +
                                "<meta property=\"og:image\" content=\"https://contents-cdn.viewus.co.kr/image/2024/07/CP-2023-0096/image-b868c07a-8a04-44cc-9f07-a591905a3680.jpeg\">" +
                                "<meta property=\"og:description\" content=\"이동 거리 " + req.distance() + "KM / 상위 " + req.percentage() + "%\">" +
                            "</head>" +
                            "<body><script>location.href=\"http://www.caecae.kro.kr/racecasper\";</script></body>" +
                        "</html>"
                );
    }
}