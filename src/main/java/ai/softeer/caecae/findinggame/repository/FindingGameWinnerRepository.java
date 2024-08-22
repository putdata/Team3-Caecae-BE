package ai.softeer.caecae.findinggame.repository;

import ai.softeer.caecae.findinggame.domain.entity.FindingGameWinner;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FindingGameWinnerRepository extends JpaRepository<FindingGameWinner, Integer> {
    @Modifying
    @Query(value = """
            INSERT IGNORE INTO finding_game_winner (user_id, finding_game_id)
            VALUES (:userId, :gameId)
            """, nativeQuery = true)
    void insertWinner(@Param("userId") Integer userId, @Param("gameId") Integer gameId);
}
