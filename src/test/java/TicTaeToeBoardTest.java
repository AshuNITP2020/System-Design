import apis.AIEngine;
import apis.RuleEngine;
import boards.Board;
import boards.TicTaeToeBoard;
import game.Cell;
import game.GameEngine;
import game.Move;
import game.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicTaeToeBoardTest {

    RuleEngine ruleEngine;
    GameEngine gameEngine;
    AIEngine aiEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngine();
        gameEngine = new GameEngine();
        aiEngine = new AIEngine();
    }

    @Test
    void rowWinCheck() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{1, 0}, {1, 1}, {1, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
            nextIndex = playNextMove(moves, nextIndex, board);
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("X", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

    @Test
    void columnWinCheck() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{0, 0}, {1, 0}, {2, 0}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
            nextIndex = playNextMove(moves, nextIndex, board);
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("X", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

    @Test
    void diagWinCheck() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{0, 0}, {1, 1}, {2, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
            nextIndex = playNextMove(moves, nextIndex, board);
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("X", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

    @Test
    void revDiagonalWinCheck() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{2, 0}, {1, 1}, {0, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
            nextIndex = playNextMove(moves, nextIndex, board);
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("X", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

    @Test
    void opponentWinCheck() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{1, 0}, {1, 1}, {2, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
            nextIndex = playNextMove(moves, nextIndex, board);
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("O", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

    private int playNextMove(int[][] moves, int nextIndex, Board board) {
        Player player = new Player("X");
        Player opponent = new Player("O");
        int row = moves[nextIndex][0];
        int col = moves[nextIndex][1];
        nextIndex++;
        Move playerMove = new Move(new Cell(row, col), player);
        gameEngine.makeMove(playerMove);
        if (!ruleEngine.getBoardState(board).isOver()) {
            gameEngine.makeMove(aiEngine.suggestMove(opponent, board));
        }
        return nextIndex;
    }
}
