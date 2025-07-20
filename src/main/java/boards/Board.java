package boards;

import game.GameResult;
import game.Move;
import game.Player;

public interface Board {
    void makeMove(Player player, Move move);

    GameResult hasWinner();
}
