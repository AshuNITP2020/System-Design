package tictactoe.game;

public class Player {
    String symbol;

    public Player(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public Player flip() {
        return symbol == "x" ? new Player("O") : new Player("X");
    }
}
