import boards.Board;
import boards.TicTaeToeBoard;
import game.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new TicTaeToeBoard();
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(board);

        Player player = new Player("X");
        Player opponent = new Player("O");
        Scanner scanner = new Scanner(System.in);
        while (!gameEngine.isComplete(board)) {
            System.out.println("Make a move");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            Move playerMove = new Move(new Cell(row, col));
            gameEngine.makeMove(player, playerMove);

            Move opponentMove = gameEngine.suggestMove();
            gameEngine.makeMove(opponent, opponentMove);
        }

        System.out.println(gameEngine.getBoard().hasWinner().getPlayer().getSymbol());
    }
}