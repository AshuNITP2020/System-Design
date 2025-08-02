package splitWise.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import splitWise.*;
import splitWise.splitPattern.EqualSplit;
import splitWise.splitPattern.ExactSplit;
import splitWise.splitPattern.PercentSplit;
import splitWise.splitPattern.Split;

public class SplitService {
    public static Expense createEqualExpense(String expenseId, String description, double amount, User paidBy, Group group, List<User> users, Date date) {
        List<Split> splits = new ArrayList<>();
        for (User user : users) {
            splits.add(new EqualSplit(user, 0));
        }
        Expense expense = new Expense(expenseId, description, amount, paidBy, group, splits, date);
        expense.calculateSplits();
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
        expense.calculateSplits();
        return expense;
    }

    public void addExpense(Expense expense) {
        expense.calculateSplits();
    }

    public void settleUp(User user1, User user2) {
    }

    public BalanceSheet getBalanceSheet(Group group) {
        return new BalanceSheet();
    }
}
