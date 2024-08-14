package ai.softeer.caecae.findinggame.domain.entity;

import ai.softeer.caecae.user.domain.entity.User;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class FindingGameWinnerId implements Serializable {
    private User user;
    private FindingGame findingGame;

    public FindingGameWinnerId(User user, FindingGame findingGame) {
        this.user = user;
        this.findingGame = findingGame;
    }
}