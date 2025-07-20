package game;

import boards.Board;
import boards.TicTaeToeBoard;

public class GameEngine {
    Board board;

    public Board getBoard() {
        return board;
    }

    public void start(Board board) {
        if (board instanceof TicTaeToeBoard) {
            this.board = board;
        } else {
            throw new UnsupportedOperationException("Game Not Supported");
        }
    }

    public boolean isComplete(Board board) {
        return board.hasWinner().hasWinner;
    }

    public void makeMove(Player player, Move move) {
        if (board instanceof TicTaeToeBoard) {
            board.makeMove(player, move);
        } else {
            throw new UnsupportedOperationException("");
        }
    }

    public Move suggestMove() {
        if (board instanceof TicTaeToeBoard) {
            String[][] cells = ((TicTaeToeBoard) board).getCells();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cells[i][j] == null) {
                        return new Move(new Cell(i, j));
                    }
                }
            }
        }
        return null;
    }
}
