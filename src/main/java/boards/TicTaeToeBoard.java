package boards;

import game.Move;

public class TicTaeToeBoard implements Board {

    String[][] cells = new String[3][3];

    public TicTaeToeBoard() {
    }

    public String[][] getCells() {
        return cells;
    }

    @Override
    public void makeMove(Move move) {
        int row = move.getCell().getRow();
        int col = move.getCell().getCol();

        cells[row][col] = move.getPlayer().getSymbol();
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
