import boards.TicTaeToeBoard;
import game.Cell;
import game.GameResult;
import game.Move;
import game.Player;

public class GameEngine {
    public static void main(String[] args) {
        TicTaeToeBoard ticTaeToeBoard = new TicTaeToeBoard();

        Move move1 = new Move(new Cell(0, 0));
        Move move2 = new Move(new Cell(0, 1));
        Move move3 = new Move(new Cell(0, 2));
        Move move4 = new Move(new Cell(1, 0));
        Move move5 = new Move(new Cell(1, 2));
        Move move6 = new Move(new Cell(1, 1));
        Move move7 = new Move(new Cell(2, 2));
        Move move8 = new Move(new Cell(2, 0));
        Move move9 = new Move(new Cell(2, 1));

        Player player1 = new Player("O", move1);
        ticTaeToeBoard.move(player1);

        Player player2 = new Player("X", move2);
        ticTaeToeBoard.move(player2);

        player1 = new Player("O", move6);
        ticTaeToeBoard.move(player1);

        player2 = new Player("X", move3);
        ticTaeToeBoard.move(player2);
        GameResult gameResult = ticTaeToeBoard.hasWinner();
        System.out.println(gameResult.getPlayer().getSymbol());

        player1 = new Player("O", move7);
        ticTaeToeBoard.move(player1);
        gameResult = ticTaeToeBoard.hasWinner();
        System.out.println(gameResult.getPlayer().getSymbol());

        player2 = new Player("X", move4);
        ticTaeToeBoard.move(player2);
    }
}