package apis;

import boards.Board;
import boards.TicTaeToeBoard;
import game.Cell;
import game.Move;
import game.Player;

public class AIEngine {

    public Move suggestMove(Player opponent, Board board) {
        if (board instanceof TicTaeToeBoard) {
            String[][] cells = ((TicTaeToeBoard) board).getCells();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cells[i][j] == null) {
                        return new Move(new Cell(i, j), opponent);
                    }
                }
            }
        }
        return null;
    }
}
