import apis.AIEngine;
import apis.RuleEngine;
import boards.Board;
import boards.TicTaeToeBoard;
import game.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        TicTaeToeBoard board = new TicTaeToeBoard();
        GameEngine gameEngine = new GameEngine();
        AIEngine aiEngine = new AIEngine();
        GameState gameState = new GameState();
        gameEngine.start(board);

        Player player = new Player("X");
        Player opponent = new Player("O");
        Scanner scanner = new Scanner(System.in);

        while (!gameState.getBoardState(board.ruleEngine).isOver()) {
            System.out.println("Make a move");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            Move playerMove = new Move(new Cell(row, col), player);
            gameEngine.makeMove(playerMove);
            Move opponentMove = aiEngine.suggestMove(opponent, board);
            gameEngine.makeMove(opponentMove);
            System.out.println(board);
        }

        boolean hasWinner = gameState.getBoardState(board.ruleEngine).isOver();
        System.out.println("Player Won: " + (hasWinner ? gameState.getBoardState(board.ruleEngine).getPlayer().getSymbol() : "No Body"));
    }
}