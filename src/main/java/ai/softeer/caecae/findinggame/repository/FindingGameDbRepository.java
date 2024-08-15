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
    List<FindingGame> findAllByOrderByStartTime(); // TODO : 조회하는 데이터의 결과가 같아서 합쳐야 함

    @Cacheable(cacheNames = "RecentFindingGame")
    @Query(value = "SELECT f FROM FindingGame f WHERE f.id = :id")
    FindingGame findByIdCacheable(@Param("id") Integer id);

    @Cacheable(cacheNames = "AllFindingGame")
    @Query("SELECT f FROM FindingGame f ORDER BY f.startTime")
    List<FindingGame> findAllOrderByStartTimeCacheable(); // TODO : 조회하는 데이터의 결과가 같아서 합쳐야 함
}
