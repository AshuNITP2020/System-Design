package splitWise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import splitWise.entity.User;
import splitWise.entity.Group;
import splitWise.repository.UserRepository;
import splitWise.repository.GroupRepository;
import splitWise.repository.ExpenseRepository;
import splitWise.BalanceSheet;
import splitWise.entity.Expense;
import splitWise.dto.BalanceDTO;
import splitWise.dto.DebtDTO;
import java.util.*;

@RestController
@RequestMapping("/balances")
public class BalanceController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public List<BalanceDTO> getAllBalances() {
        BalanceSheet sheet = computeBalanceSheet();
        List<BalanceDTO> result = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            Map<String, Double> userBalances = new HashMap<>();
            for (Map.Entry<User, Double> entry : sheet.getUserBalances(u).entrySet()) {
                userBalances.put(entry.getKey().getName(), entry.getValue());
            }
            result.add(new BalanceDTO(u.getName(), userBalances));
        }
        return result;
    }

    @GetMapping("/user/{userId}")
    public BalanceDTO getUserBalances(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        BalanceSheet sheet = computeBalanceSheet();
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<User, Double> entry : sheet.getUserBalances(user).entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return new BalanceDTO(user.getName(), result);
    }

    @GetMapping("/group/{groupId}")
    public List<BalanceDTO> getGroupBalances(@PathVariable Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) return Collections.emptyList();
        BalanceSheet sheet = computeBalanceSheet();
        List<BalanceDTO> result = new ArrayList<>();
        for (User u : group.getMembers()) {
            Map<String, Double> userBalances = new HashMap<>();
            for (Map.Entry<User, Double> entry : sheet.getUserBalances(u).entrySet()) {
                if (group.getMembers().contains(entry.getKey())) {
                    userBalances.put(entry.getKey().getName(), entry.getValue());
                }
            }
            result.add(new BalanceDTO(u.getName(), userBalances));
        }
        return result;
    }

    @PostMapping("/settle")
    public String settleUp(@RequestParam Long fromUserId, @RequestParam Long toUserId, @RequestParam double amount) {
        User from = userRepository.findById(fromUserId).orElse(null);
        User to = userRepository.findById(toUserId).orElse(null);
        if (from == null || to == null) return "Invalid users";
        return from.getName() + " settled up " + amount + " with " + to.getName();
    }

    @GetMapping("/debts")
    public List<DebtDTO> getAllDebts() {
        BalanceSheet sheet = computeBalanceSheet();
        List<DebtDTO> debts = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User from : users) {
            Map<User, Double> balances = sheet.getUserBalances(from);
            for (Map.Entry<User, Double> entry : balances.entrySet()) {
                User to = entry.getKey();
                double amount = entry.getValue();
                if (amount < 0) {
                    debts.add(new DebtDTO(from.getName(), to.getName(), -amount));
                }
            }
        }
        return debts;
    }

    private BalanceSheet computeBalanceSheet() {
        BalanceSheet sheet = new BalanceSheet();
        List<Expense> expenses = expenseRepository.findAll();
        for (Expense expense : expenses) {
            if (expense.getSplits() != null) {
                for (splitWise.splitPattern.Split split : expense.getSplits()) {
                    if (!split.getUser().equals(expense.getPaidBy())) {
                        sheet.updateBalance(split.getUser(), expense.getPaidBy(), split.getAmount());
                    }
                }
            }
        }
        return sheet;
    }
}
