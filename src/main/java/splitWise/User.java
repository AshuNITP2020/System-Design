package splitWise;

import splitWise.splitPattern.Split;

import java.util.*;

public class User {
    private String userId;
    private String name;
    private String email;
    private List<Group> groups;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.groups = new ArrayList<>();
    }

    public void addExpense(Expense expense, BalanceSheet balanceSheet) {
        for (Group group : groups) {
            if (group.getMembers().contains(this)) {
                group.addExpense(expense);
            }
        }

        expense.calculateSplits();
        for (Split split : expense.getSplits()) {
            if (!split.getUser().equals(expense.getPaidBy())) {
                balanceSheet.updateBalance(split.getUser(), expense.getPaidBy(), split.getAmount());
            }
        }
    }

    public void joinGroup(Group group) {
        if (!groups.contains(group)) {
            groups.add(group);
            group.addMember(this);
        }
    }

    public void printBalances(BalanceSheet balanceSheet) {
        Map<User, Double> balances = balanceSheet.getUserBalances(this);
        for (Map.Entry<User, Double> entry : balances.entrySet()) {
            if (entry.getValue() > 0.0) {
                System.out.println(this.name + " is owed " + entry.getValue() + " by " + entry.getKey().name);
            } else if (entry.getValue() < 0.0) {
                System.out.println(this.name + " owes " + (-entry.getValue()) + " to " + entry.getKey().name);
            }
        }
    }

    public String getName() { return name; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public List<Group> getGroups() { return groups; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
