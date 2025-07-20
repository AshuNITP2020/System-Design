package game;

public class Player {
    String symbol;
    Move move;

    public Player(String symbol, Move move) {
        this.symbol = symbol;
        this.move = move;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
