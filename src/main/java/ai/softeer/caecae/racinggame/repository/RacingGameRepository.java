package ai.softeer.caecae.racinggame.repository;

import ai.softeer.caecae.racinggame.domain.entity.RacingGameParticipant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacingGameRepository extends JpaRepository<RacingGameParticipant, Integer> {

    @Query(value = "SELECT p FROM RacingGameParticipant p ORDER BY ABS(p.distance - :offset) ASC")
    List<RacingGameParticipant> findAllByAdjustedDistance(@Param("offset") double offset);

    @Cacheable(cacheNames = "CalculatedGapRankedList")
    @Query(value = """
             SELECT
                 distance_gap
             FROM (
                 SELECT
                     ROW_NUMBER() OVER (PARTITION BY rank_group ORDER BY distance_gap) AS row_num,
                     rank_group,
                     distance_gap
                 FROM (
                     SELECT
                         NTILE(100) OVER (ORDER BY ABS(315 - distance)) AS rank_group,
                         ABS(315 - distance) AS distance_gap
                         FROM racing_game_participant
                     ) AS ranked_data
                 ) AS numbered_data
             WHERE row_num = 1;
            """, nativeQuery = true)
    List<Double> getCalculatedGapRankedList();
}
