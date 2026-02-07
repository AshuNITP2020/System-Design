package splitWise.dto;

import java.util.Date;
import java.util.List;

public class AddExpenseRequest {
    private String expenseId;
    private String description;
    private double amount;
    private Long paidById;
    private Long groupId;
    private Date date;
    private String splitType; // "EQUAL", "EXACT", "PERCENT"
    private List<Long> userIds;
    private List<Double> exactAmounts;
    private List<Double> percents;

    public AddExpenseRequest() {}

    public AddExpenseRequest(String expenseId, String description, double amount, Long paidById, Long groupId, Date date, String splitType, List<Long> userIds, List<Double> exactAmounts, List<Double> percents) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.paidById = paidById;
        this.groupId = groupId;
        this.date = date;
        this.splitType = splitType;
        this.userIds = userIds;
        this.exactAmounts = exactAmounts;
        this.percents = percents;
    }

    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Long getPaidById() { return paidById; }
    public void setPaidById(Long paidById) { this.paidById = paidById; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
    public List<Double> getExactAmounts() { return exactAmounts; }
    public void setExactAmounts(List<Double> exactAmounts) { this.exactAmounts = exactAmounts; }
    public List<Double> getPercents() { return percents; }
    public void setPercents(List<Double> percents) { this.percents = percents; }
}

