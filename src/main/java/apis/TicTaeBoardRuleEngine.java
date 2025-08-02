package apis;

import boards.Board;
import boards.TicTaeToeBoard;
import game.GameResult;
import game.Player;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TicTaeBoardRuleEngine extends RuleEngine {

    public TicTaeBoardRuleEngine(Board board) {
        super(board);
        ((TicTaeToeBoard) board).ruleEngine = this;
        setRules((TicTaeToeBoard) board);
    }

    private static GameResult checkForGameCompletionWithCount(Board ticTaeToeBoard) {
        int countOfCellsFilled = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (ticTaeToeBoard.getSymbol(i, j) != null) {
                    countOfCellsFilled++;
                }
            }
        }
        if (countOfCellsFilled == 9) {
            return new GameResult(true, new Player("_"));
        }
        return new GameResult(false, new Player("_"));
    }

    public void setRules(TicTaeToeBoard board) {
        List<Rule> rules = List.of(
                new Rule<>(ticTaeToeBoard -> traverseRowAndCol((i, j) -> ticTaeToeBoard.getSymbol(i, j))),
                new Rule<>(ticTaeToeBoard -> traverseRowAndCol((i, j) -> ticTaeToeBoard.getSymbol(j, i))),
                new Rule<>(ticTaeToeBoard -> traverseDiag(i -> ticTaeToeBoard.getSymbol(i, i))),
                new Rule<>(ticTaeToeBoard -> traverseDiag(i -> ticTaeToeBoard.getSymbol(2 - i, i))),
                new Rule<>(TicTaeBoardRuleEngine::checkForGameCompletionWithCount)
        );

        board.ruleEngine.setRules(rules);
    }

    GameResult traverseDiag(Function<Integer, String> diag) {
        return traverse(diag);
    }

    GameResult traverseRowAndCol(BiFunction<Integer, Integer, String> biFunction) {
        GameResult result = new GameResult(false, new Player("_"));
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            Function<Integer, String> function = (a) -> biFunction.apply(finalI, a);
            result = traverse(function);
            if (result.isOver()) return result;
        }
        return result;
    }

    GameResult traverse(Function<Integer, String> func) {
        GameResult result = new GameResult(false, new Player("_"));
        boolean diagonalFound = true;
        for (int i = 0; i < 3; i++) {
            if (func.apply(i) == null || !func.apply(0).equals(func.apply(i))) {
                diagonalFound = false;
                break;
            }
        }
        if (diagonalFound) {
            result = new GameResult(true, new Player(func.apply(0)));
        }
        return result;
    }
}
