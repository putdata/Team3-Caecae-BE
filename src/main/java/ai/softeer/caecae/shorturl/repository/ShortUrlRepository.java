package ai.softeer.caecae.shorturl.repository;

import ai.softeer.caecae.shorturl.domain.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Integer> {
    Optional<ShortUrlEntity> findByShortUrl(String shortUrl);

    Optional<ShortUrlEntity> findByLongUrl(String longUrl);
}
