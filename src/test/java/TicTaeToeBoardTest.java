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
    void shouldReturnTicTaeBoardRuleEngineForTicTaeToeBoard() {
        Board board = new TicTaeToeBoard();
        int[][] moves = new int[][]{{1, 0}, {1, 1}, {1, 2}};
        int nextIndex = 0;
        gameEngine.start(board);

        while (!ruleEngine.getBoardState(board).isOver()) {
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
        }

        assertTrue(ruleEngine.getBoardState(board).isOver());
        assertEquals("X", ruleEngine.getBoardState(board).getPlayer().getSymbol());
    }

}
