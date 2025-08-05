package splitWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitWise.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Expense findByExpenseId(String expenseId);
}

