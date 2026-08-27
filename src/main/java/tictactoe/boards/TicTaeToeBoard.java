package tictactoe.boards;


import tictactoe.apis.RuleEngine;
import tictactoe.apis.TicTaeBoardRuleEngine;
import tictactoe.game.Move;

public class TicTaeToeBoard implements Board {
    public RuleEngine ruleEngine;
    String[][] cells = new String[3][3];

    public TicTaeToeBoard() {
        ruleEngine = new TicTaeBoardRuleEngine(this);
    }

    public String[][] getCells() {
        return cells;
    }

    @Override
    public void makeMove(Move move) {
        int row = move.getCell().getRow();
        int col = move.getCell().getCol();
        if (cells[row][col] == null) {
            cells[row][col] = move.getPlayer().getSymbol();
        } else {
            throw new IllegalArgumentException("Wrong Move");
        }
    }

    /**
     * Prototype design Pattern, a clone() should be defined in the class which copy is to be made.
     */
    @Override
    public TicTaeToeBoard clone() {
        TicTaeToeBoard ticTaeToeBoard = new TicTaeToeBoard();
        for (int i = 0; i < 3; i++) {
            System.arraycopy(cells[i], 0, ticTaeToeBoard.cells[i], 0, 3);
        }
        return ticTaeToeBoard;
    }

    @Override
    public String getSymbol(int row, int col) {
        return cells[row][col];
    }

    @Override
    public String toString() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String cell = cells[i][j] == null ? "_" : cells[i][j];
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        return "TicTaeToeBoard";
    }
}
