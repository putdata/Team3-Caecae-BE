package ai.softeer.caecae.findinggame.domain.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindingGameRealWinner {
    @Column(nullable = false)
    private int gameId;

    @Column(nullable = false)
    private String phone;
}
