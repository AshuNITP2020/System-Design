package game;

public class GameResult {
    boolean hasWinner;
    Player player;

    public GameResult(boolean hasWinner, Player player) {
        this.hasWinner = hasWinner;
        this.player = player;
    }

    public boolean hasWinner() {
        return hasWinner;
    }

    public void setHasWinner(boolean hasWinner) {
        this.hasWinner = hasWinner;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
