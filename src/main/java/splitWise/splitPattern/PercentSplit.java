package splitWise.splitPattern;

import splitWise.User;

public class PercentSplit extends Split {
    private double percent;
    public PercentSplit(User user, double percent) {
        super(user, 0);
        this.percent = percent;
    }
    public double getPercent() {
        return percent;
    }
}
