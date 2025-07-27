package boards;

import game.Move;

public interface Board {
    void makeMove(Move move);
    Board clone();
    String getSymbol(int row, int col);
}
