package splitWise;

import splitWise.splitPattern.EqualSplit;
import splitWise.splitPattern.PercentSplit;
import splitWise.splitPattern.Split;

import java.util.*;

public class Expense {
    private String expenseId;
    private String description;
    private double amount;
    private User paidBy;
    private Group group;
    private List<Split> splits;
    private Date date;

    public Expense(String expenseId, String description, double amount, User paidBy, Group group, List<Split> splits, Date date) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.group = group;
        this.splits = splits;
        this.date = date;
    }

    public void calculateSplits() {
        if (splits == null || splits.isEmpty()) return;
        if (splits.get(0) instanceof EqualSplit) {
            double splitAmount = Math.round((amount / splits.size()) * 100.0) / 100.0;
            for (Split split : splits) {
                split.setAmount(splitAmount);
            }
        } else if (splits.get(0) instanceof PercentSplit) {
            for (Split split : splits) {
                PercentSplit ps = (PercentSplit) split;
                split.setAmount(Math.round((amount * ps.getPercent() / 100.0) * 100.0) / 100.0);
            }
        }
    }

    public String getExpenseId() { return expenseId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public Group getGroup() { return group; }
    public List<Split> getSplits() { return splits; }
    public Date getDate() { return date; }
}
