package tictactoe.apis;

import tictactoe.boards.Board;

import java.util.List;

public abstract class RuleEngine {
    private final Board board;
    private List<Rule> rules;

    public RuleEngine(Board board) {
        this.board = board;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Board getBoard() {
        return board;
    }
}

