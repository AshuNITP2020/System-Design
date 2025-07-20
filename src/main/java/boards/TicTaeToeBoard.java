package boards;

import game.Cell;
import game.GameResult;
import game.Move;
import game.Player;

public class TicTaeToeBoard implements Board {

    String[][] cells = new String[3][3];

    public String[][] getCells() {
        return cells;
    }

    public TicTaeToeBoard() {
    }

    @Override
    public void makeMove(Player player, Move move) {
        int row = move.getCell().getRow();
        int col = move.getCell().getCol();

        cells[row][col] = player.getSymbol();
    }

    @Override
    public GameResult hasWinner() {
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
            return new GameResult(false, new Player(""));
        }

        return new GameResult(false, new Player(""));
    }
}
