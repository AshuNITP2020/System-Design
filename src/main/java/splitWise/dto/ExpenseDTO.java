package splitWise.dto;

import java.util.Date;

public class ExpenseDTO {
    private Long id;
    private String expenseId;
    private String description;
    private double amount;
    private Long paidById;
    private Long groupId;
    private Date date;

    public ExpenseDTO() {}

    public ExpenseDTO(Long id, String expenseId, String description, double amount, Long paidById, Long groupId, Date date) {
        this.id = id;
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.paidById = paidById;
        this.groupId = groupId;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}
