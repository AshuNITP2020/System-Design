package splitWise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import splitWise.dto.AddExpenseRequest;
import splitWise.dto.ExpenseDTO;
import splitWise.service.ExpenseService;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ExpenseDTO createExpense(@RequestBody AddExpenseRequest request) {
        return expenseService.createExpense(request);
    }

    @GetMapping("/{id}")
    public ExpenseDTO getExpense(@PathVariable Long id) {
        return expenseService.getExpense(id);
    }

    @GetMapping
    public List<ExpenseDTO> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @PutMapping("/{id}")
    public ExpenseDTO updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        return expenseService.updateExpense(id, expenseDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }
}
