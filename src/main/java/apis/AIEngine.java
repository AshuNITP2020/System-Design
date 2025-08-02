package apis;

import boards.Board;
import boards.TicTaeToeBoard;
import game.Cell;
import game.GameState;
import game.Move;
import game.Player;

public class AIEngine {

    public Move suggestMove(Player player, Board board) {
        if (board instanceof TicTaeToeBoard ticTaeToeBoard) {
            String[][] cells = ticTaeToeBoard.getCells();
            Move move;
            if (boardCompletedThreeMoves(cells)) {
                move = getSmartMove(player, ticTaeToeBoard);
            } else {
                move = getBasicMove(player, cells);
            }
            if (move != null) return move;
        }
        return null;
    }

    private boolean boardCompletedThreeMoves(String[][] cells) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] != null) {
                    count++;
                }
            }
        }
        return count < 3;
    }

    private static Move getBasicMove(Player player, String[][] cells) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == null) {
                    return new Move(new Cell(i, j), player);
                }
            }
        }
        return null;
    }

    public Move getSmartMove(Player player, TicTaeToeBoard board) {
        TicTaeToeBoard boardCopy = board.clone();
        GameState gameState = new GameState();
        String[][] cells = boardCopy.getCells();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == null) {
                    Move move =  new Move(new Cell(i, j), player);
                    boardCopy.makeMove(move);
                    if (gameState.getBoardState(board.ruleEngine).isOver()) {
                        return move;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == null) {
                    Move move =  new Move(new Cell(i, j), player.flip());
                    boardCopy.makeMove(move);
                    if (gameState.getBoardState(board.ruleEngine).isOver()) {
                        return move;
                    }
                }
            }
        }

        return getBasicMove(player, board.getCells());
    }
}
