package ai.softeer.caecae.findinggame.repository;

import ai.softeer.caecae.findinggame.domain.entity.FindingGame;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Redis Repository 도 만들어야 하므로 네이밍에 Db를 붙임
@Repository
public interface FindingGameDbRepository extends JpaRepository<FindingGame, Integer> {
    List<FindingGame> findAllByOrderByStartTime();

    @Cacheable(cacheNames = "RecentFindingGame")
    @Query(value = "SELECT f FROM FindingGame f WHERE f.id = :id")
    FindingGame findByIdCacheable(@Param("id") Integer id);
}
