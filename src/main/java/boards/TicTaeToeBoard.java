package boards;

import game.GameResult;
import game.Player;

public class TicTaeToeBoard implements Board {

    String[][] cells = new String[3][3];

    public TicTaeToeBoard() {
    }

    public void move(Player player) {
        int row = player.getMove().getCell().getRow();
        int col = player.getMove().getCell().getCol();

        cells[row][col] = player.getSymbol();
    }

    public GameResult hasWinner() {
        boolean rowFound = true;
        String symbol = "";
        for (int i = 0; i < 3; i++) {
            symbol = cells[i][0];
            for (int j = 1; j < 3; j++) {
                if (!(cells[i][j] == symbol)) {
                    rowFound = false;
                    break;
                }
            }
        }
        if (rowFound) {
            return new GameResult(true, new Player(symbol, null));
        }

        boolean colFound = true;

        for (int i = 0; i < 3; i++) {
            symbol = cells[0][i];
            for (int j = 0; j < 3; j++) {
                if (!(cells[j][i] == symbol)) {
                    colFound = false;
                    break;
                }
            }
        }

        if (colFound) {
            return new GameResult(true, new Player(symbol, null));
        }

        boolean diagonalFound = true;

        for (int i = 1; i < 3; i++) {
            symbol = cells[0][0];
            if (!(cells[i][i] == symbol)) {
                diagonalFound = false;
                break;
            }
        }

        if (diagonalFound) {
            return new GameResult(true, new Player(symbol, null));
        }

        boolean revDiagonalFound = true;

        for (int i = 1; i < 3; i++) {
            symbol = cells[2][0];
            if (!(cells[2 - i][i] == symbol)) {
                revDiagonalFound = false;
                break;
            }
        }

        if (revDiagonalFound) {
            return new GameResult(true, new Player(symbol, null));
        }

        return new GameResult(false, new Player("", null));
    }
}
