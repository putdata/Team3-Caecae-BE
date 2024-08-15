package ai.softeer.caecae.findinggame.repository;

import ai.softeer.caecae.findinggame.domain.entity.FindingGameAnswer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FindingGameAnswerDbRepository extends JpaRepository<FindingGameAnswer, Integer> {
    @Cacheable(value = "AllFindingGameAnswersByGameId", key = "#findingGameId")
    public List<FindingGameAnswer> findAllByFindingGame_Id(Integer findingGameId);

}
