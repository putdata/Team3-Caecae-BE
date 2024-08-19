package ai.softeer.caecae.shorturl.api;

import ai.softeer.caecae.global.dto.response.SuccessResponse;
import ai.softeer.caecae.global.enums.SuccessCode;
import ai.softeer.caecae.shorturl.domain.request.ShareUrlRequestDto;
import ai.softeer.caecae.shorturl.domain.response.ShareUrlResponseDto;
import ai.softeer.caecae.shorturl.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/url")
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    /**
     * 레이싱 게임에서 공유하기 버튼을 눌렀을 때 짧은 주소를 만들어주는 API
     *
     * @param req
     * @return shortUrl
     */
    @PostMapping("/share")
    public ResponseEntity<SuccessResponse<ShareUrlResponseDto>> shareUrl(@RequestBody ShareUrlRequestDto req) {
        return SuccessResponse.of(SuccessCode.OK, shortUrlService.shareUrl(req));
    }


    /**
     * 단축 URL을 원래 URL로 리다렉션 시켜주는 API
     *
     * @param shortUrl
     * @return 리다렉션 헤더가 포함된 ResponseEntity
     */
    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> shortUrl(@PathVariable String shortUrl) {
        return shortUrlService.shortUrlToLongUrl(shortUrl);
    }

    /**
     * 동적으로 변하는 데이터를 카카오톡 미리보기로 볼 수 있게 하기 위한 API
     *
     * @param req
     * @return head 및 강제 url 이동하는 text/html
     */
    @GetMapping("/preview")
    public ResponseEntity<?> previewShareUrl(ShareUrlRequestDto req) {
        return shortUrlService.previewUrl(req);
    }
}
