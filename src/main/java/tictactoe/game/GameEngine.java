package tictactoe.game;


import tictactoe.boards.Board;
import tictactoe.boards.TicTaeToeBoard;

public class GameEngine {
    Board board;

    public void start(Board board) {
        if (board instanceof TicTaeToeBoard) {
            this.board = board;
        } else {
            throw new UnsupportedOperationException("Game Not Supported");
        }
    }

    public void makeMove(Move move) {
        if (board instanceof TicTaeToeBoard) {
            board.makeMove(move);
        } else {
            throw new UnsupportedOperationException("");
        }
    }

}
