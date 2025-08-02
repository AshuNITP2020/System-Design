package tictactoe.apis;

import tictactoe.boards.Board;
import tictactoe.game.GameResult;
import java.util.function.Function;

public class Rule<T extends Board> {
    private final Function<T, GameResult> ruleFunction;

    public Rule(Function<T, GameResult> ruleFunction) {
        this.ruleFunction = ruleFunction;
    }

    public GameResult apply(T board) {
        return ruleFunction.apply(board);
    }
}
