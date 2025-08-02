package splitWise;

import java.util.*;

public class Group {
    private String groupId;
    private String name;
    private List<User> members;
    private List<Expense> expenses;

    public Group(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
        this.members = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
        }
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void printGroupExpenses() {
        for (Expense expense : expenses) {
            System.out.println("Expense: " + expense.getDescription() + ", Amount: " + expense.getAmount() + ", Paid by: " + expense.getPaidBy().getName());
        }
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}
