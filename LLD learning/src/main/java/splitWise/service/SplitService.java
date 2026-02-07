package splitWise.service;

import org.springframework.stereotype.Service;
import splitWise.*;
import splitWise.entity.Expense;
import splitWise.entity.Group;
import splitWise.entity.User;
import splitWise.splitPattern.*;
import java.util.*;

@Service
public class SplitService {
    public static Expense createEqualExpense(String expenseId, String description, double amount, User paidBy, Group group, List<User> users, Date date) {
        List<Split> splits = new ArrayList<>();
        for (User user : users) {
            splits.add(new EqualSplit(user, 0));
        }
        Expense expense = new Expense(expenseId, description, amount, paidBy, group, splits, date);
        calculateSplits(expense);
        return expense;
    }

    public static Expense createExactExpense(String expenseId, String description, double amount, User paidBy, Group group, List<Double> exactAmounts, List<User> users, Date date) {
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            splits.add(new ExactSplit(users.get(i), exactAmounts.get(i)));
        }
        Expense expense = new Expense(expenseId, description, amount, paidBy, group, splits, date);
        return expense;
    }

    public static Expense createPercentExpense(String expenseId, String description, double amount, User paidBy, Group group, List<Double> percents, List<User> users, Date date) {
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            splits.add(new PercentSplit(users.get(i), percents.get(i)));
        }
        Expense expense = new Expense(expenseId, description, amount, paidBy, group, splits, date);
        calculateSplits(expense);
        return expense;
    }

    public void settleUp(User user1, User user2) {
    }

    public BalanceSheet getBalanceSheet(Group group) {
        return new BalanceSheet();
    }

    public static void calculateSplits(Expense expense) {
        List<Split> splits = expense.getSplits();
        if (splits == null || splits.isEmpty()) return;
        if (splits.get(0) instanceof EqualSplit) {
            double splitAmount = Math.round((expense.getAmount() / splits.size()) * 100.0) / 100.0;
            for (Split split : splits) {
                split.setAmount(splitAmount);
            }
        } else if (splits.get(0) instanceof PercentSplit) {
            for (Split split : splits) {
                PercentSplit ps = (PercentSplit) split;
                split.setAmount(Math.round((expense.getAmount() * ps.getPercent() / 100.0) * 100.0) / 100.0);
            }
        }
    }
}
