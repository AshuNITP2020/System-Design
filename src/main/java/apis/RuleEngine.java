package apis;

import boards.Board;
import boards.TicTaeToeBoard;
import game.GameResult;
import game.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RuleEngine {

    public GameResult getBoardState(Board board) {
        if (board instanceof TicTaeToeBoard) {
            GameResult result;
            BiFunction<Integer, Integer, String> biFunctionRow = (i, j) -> board.getSymbol(i, j);
            BiFunction<Integer, Integer, String> biFunctionCol = (i, j) -> board.getSymbol(j, i);
            Function<Integer, String> diag = (i) -> board.getSymbol(i, i);
            Function<Integer, String> revdiag = (i) -> board.getSymbol(2 - i, i);

            result = traverseRowAndCol(biFunctionRow);
            if (result.isOver()) return result;

            result = traverseRowAndCol(biFunctionCol);
            if (result.isOver()) return result;

            result = traverseDiag(diag);
            if (result.isOver()) return result;

            result = traverseDiag(revdiag);
            if (result.isOver()) return result;

            int countOfCellsFilled = 0;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.getSymbol(i, j) != null) {
                        countOfCellsFilled++;
                    }
                }
            }

            if (countOfCellsFilled == 9) {
                result = new GameResult(true, new Player("_"));
            }
            return result;
        } else {
            throw new UnsupportedOperationException("");
        }
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
