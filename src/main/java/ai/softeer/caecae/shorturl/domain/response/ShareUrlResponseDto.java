package ai.softeer.caecae.shorturl.domain.response;

import lombok.Builder;

@Builder
public record ShareUrlResponseDto(
        String shortUrl
) {
}
