package splitWise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitWise.dto.AddExpenseRequest;
import splitWise.dto.ExpenseDTO;
import splitWise.entity.Expense;
import splitWise.entity.Group;
import splitWise.entity.User;
import splitWise.repository.ExpenseRepository;
import splitWise.repository.GroupRepository;
import splitWise.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SplitService splitService;

    public ExpenseDTO createExpense(AddExpenseRequest request) {
        User paidBy = userRepository.findById(request.getPaidById()).orElse(null);
        Group group = groupRepository.findById(request.getGroupId()).orElse(null);
        List<User> users = request.getUserIds() != null ? request.getUserIds().stream().map(id -> userRepository.findById(id).orElse(null)).collect(Collectors.toList()) : null;
        Expense expense = null;
        String expenseId = request.getExpenseId() != null ? request.getExpenseId() : java.util.UUID.randomUUID().toString();
        if (request.getSplitType().equalsIgnoreCase("EQUAL")) {
            expense = SplitService.createEqualExpense(expenseId, request.getDescription(), request.getAmount(), paidBy, group, users, request.getDate());
        } else if (request.getSplitType().equalsIgnoreCase("EXACT")) {
            expense = SplitService.createExactExpense(expenseId, request.getDescription(), request.getAmount(), paidBy, group, request.getExactAmounts(), users, request.getDate());
        } else if (request.getSplitType().equalsIgnoreCase("PERCENT")) {
            expense = SplitService.createPercentExpense(expenseId, request.getDescription(), request.getAmount(), paidBy, group, request.getPercents(), users, request.getDate());
        } else {
            throw new IllegalArgumentException("Invalid split type");
        }
        expense = expenseRepository.save(expense);
        return toDTO(expense);
    }

    public ExpenseDTO getExpense(Long id) {
        return expenseRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Optional<Expense> expenseOpt = expenseRepository.findById(id);
        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();
            expense.setDescription(expenseDTO.getDescription());
            expense.setAmount(expenseDTO.getAmount());
            expense.setDate(expenseDTO.getDate());
            expense = expenseRepository.save(expense);
            return toDTO(expense);
        }
        return null;
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    private ExpenseDTO toDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseId(expense.getExpenseId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        if (expense.getPaidBy() != null) dto.setPaidById(expense.getPaidBy().getId());
        if (expense.getGroup() != null) dto.setGroupId(expense.getGroup().getId());
        return dto;
    }
}
