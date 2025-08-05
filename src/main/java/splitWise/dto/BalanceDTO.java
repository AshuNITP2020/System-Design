package splitWise.dto;

import java.util.Map;

public class BalanceDTO {
    private String user;
    private Map<String, Double> balances;

    public BalanceDTO() {}

    public BalanceDTO(String user, Map<String, Double> balances) {
        this.user = user;
        this.balances = balances;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public Map<String, Double> getBalances() { return balances; }
    public void setBalances(Map<String, Double> balances) { this.balances = balances; }
}

