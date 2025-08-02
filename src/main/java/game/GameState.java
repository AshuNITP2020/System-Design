package game;

import apis.RuleEngine;
import apis.Rule;

public class GameState {
    public GameResult getBoardState(RuleEngine ruleEngine) {
        if (ruleEngine.getRules() == null || ruleEngine.getRules().isEmpty()) {
            return new GameResult(false, null);
        }
        for (Rule rule : ruleEngine.getRules()) {
            GameResult result = rule.apply(ruleEngine.getBoard());
            if (result.isOver()) {
                return result;
            }
        }
        return new GameResult(false, null);
    }
}
