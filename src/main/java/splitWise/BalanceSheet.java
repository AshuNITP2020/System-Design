package splitWise;

import java.util.*;

public class BalanceSheet {
    /***
     * This represent user balances to other users.
     * The key is the user who owes money, and the value is a map of users to the amount owed.
     */
    private Map<User, Map<User, Double>> balances;

    public BalanceSheet() {
        this.balances = new HashMap<>();
    }

    public void updateBalance(User from, User to, double amount) {
        balances.putIfAbsent(from, new HashMap<>());
        balances.putIfAbsent(to, new HashMap<>());
        balances.get(from).put(to, balances.get(from).getOrDefault(to, 0.0) + amount);
        balances.get(to).put(from, balances.get(to).getOrDefault(from, 0.0) - amount);
    }

    public Map<User, Double> getUserBalances(User user) {
        return balances.getOrDefault(user, new HashMap<>());
    }
}

