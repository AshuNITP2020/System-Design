package apis;

import boards.Board;
import boards.TicTaeToeBoard;
import game.GameResult;
import game.Player;

public class RuleEngine {

    public GameResult getBoardState(Board board) {
        if (board instanceof TicTaeToeBoard) {
            String[][] cells = ((TicTaeToeBoard) board).getCells();
            boolean rowFound = true;
            String symbol = "";
            for (int i = 0; i < 3; i++) {
                symbol = cells[i][0];
                rowFound = symbol != null;
                if (rowFound) {
                    for (int j = 1; j < 3; j++) {
                        if (!(cells[i][j] == symbol)) {
                            rowFound = false;
                            break;
                        }
                    }
                }
                if (rowFound) {
                    break;
                }
            }
            if (rowFound) {
                return new GameResult(true, new Player(symbol));
            }

            boolean colFound = true;

            for (int i = 0; i < 3; i++) {
                symbol = cells[0][i];
                colFound = symbol != null;
                if (colFound) {
                    for (int j = 0; j < 3; j++) {
                        if (!(cells[j][i] == symbol)) {
                            colFound = false;
                            break;
                        }
                    }
                }
                if (colFound) {
                    break;
                }
            }

            if (colFound) {
                return new GameResult(true, new Player(symbol));
            }

            boolean diagonalFound = true;

            for (int i = 1; i < 3; i++) {
                symbol = cells[0][0];
                diagonalFound = symbol != null;
                if (diagonalFound) {
                    if (!(cells[i][i] == symbol)) {
                        diagonalFound = false;
                        break;
                    }
                }
            }

            if (diagonalFound) {
                return new GameResult(true, new Player(symbol));
            }

            boolean revDiagonalFound = true;

            for (int i = 1; i < 3; i++) {
                symbol = cells[2][0];
                revDiagonalFound = symbol != null;
                if (revDiagonalFound) {
                    if (!(cells[2 - i][i] == symbol)) {
                        revDiagonalFound = false;
                        break;
                    }
                }
            }

            if (revDiagonalFound) {
                return new GameResult(true, new Player(symbol));
            }

            int countOfCellsFilled = 0;

            for (int i = 0; i < 3; i++) {
                for (int j = 1; j < 3; j++) {
                    if (cells[i][j] != null) {
                        countOfCellsFilled++;
                    }
                }
            }

            if (countOfCellsFilled == 9) {
                return new GameResult(true, new Player(""));
            }

            return new GameResult(false, new Player(""));
        } else {
            throw new UnsupportedOperationException("");
        }
    }

    RuleEngine getRuleEngine(Board board) {
        if (board instanceof TicTaeToeBoard) {
            return new TicTaeBoardRuleEngine();
        }
        throw new UnsupportedOperationException("No RuleEngine for: " + board.getClass().getName());
    }
}
