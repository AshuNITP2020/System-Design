package tictactoe.game;

public class GameResult {
    boolean isOver;
    Player player;

    public GameResult(boolean isOver, Player player) {
        this.isOver = isOver;
        this.player = player;
    }

    public boolean isOver() {
        return isOver;
    }

    public Player getPlayer() {
        return player;
    }
}
