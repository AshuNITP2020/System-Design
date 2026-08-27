
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tictactoe.apis.AIEngine;
import tictactoe.boards.Board;
import tictactoe.boards.TicTaeToeBoard;
import tictactoe.game.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicTaeToeBoardTest {

    GameEngine gameEngine;
    AIEngine aiEngine;
    Board board;
    GameState gameState;

    @BeforeEach
    void setUp() {
        board = new TicTaeToeBoard();
        gameEngine = new GameEngine();
        aiEngine = new AIEngine();
        gameState = new GameState();
    }

    @Test
    void rowWinCheck() {
        int[][] moves = new int[][]{{1, 0}, {1, 1}, {1, 2}};
        int[][] aimoves = new int[][]{{0, 0}, {2, 1}, {2, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver()) {
            nextIndex = playNextMove(moves, aimoves, nextIndex, board);
        }

        assertTrue(gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver());
        assertEquals("X", gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).getPlayer().getSymbol());
    }

    @Test
    void columnWinCheck() {
        int[][] moves = new int[][]{{0, 0}, {1, 0}, {2, 0}};
        int[][] aiMoves = new int[][]{{0, 1}, {1, 1}, {2, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver()) {
            nextIndex = playNextMove(moves, aiMoves, nextIndex, board);
        }

        assertTrue(gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver());
        assertEquals("X", gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).getPlayer().getSymbol());
    }

    @Test
    void diagWinCheck() {
        int[][] moves = new int[][]{{0, 0}, {1, 1}, {2, 2}};
        int[][] aimoves = new int[][]{{1, 0}, {2, 1}, {1, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver()) {
            nextIndex = playNextMove(moves, aimoves, nextIndex, board);
        }

        assertTrue(gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver());
        assertEquals("X", gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).getPlayer().getSymbol());
    }

    @Test
    void revDiagonalWinCheck() {
        int[][] moves = new int[][]{{2, 0}, {1, 1}, {0, 2}};
        int[][] aimoves = new int[][]{{0, 0}, {2, 1}, {2, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver()) {
            nextIndex = playNextMove(moves, aimoves, nextIndex, board);
        }

        assertTrue(gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver());
        assertEquals("X", gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).getPlayer().getSymbol());
    }

    private int playNextMove(int[][] moves, int[][] aimoves,  int nextIndex, Board board) {
        Player player = new Player("X");
        Player opponent = new Player("O");
        int row = moves[nextIndex][0];
        int col = moves[nextIndex][1];
        int airow = aimoves[nextIndex][0];
        int aicol = aimoves[nextIndex][1];

        Move playerMove = new Move(new Cell(row, col), player);
        gameEngine.makeMove(playerMove);
        if (!gameState.getBoardState(((TicTaeToeBoard) board).ruleEngine).isOver()) {
            gameEngine.makeMove(new Move(new Cell(airow, aicol), opponent));
        }
        nextIndex++;
        return nextIndex;
    }
}
