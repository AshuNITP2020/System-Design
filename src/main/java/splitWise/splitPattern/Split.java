package splitWise.splitPattern;

import splitWise.entity.User;

public abstract class Split {
    protected User user;
    protected double amount;

    public Split(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}

