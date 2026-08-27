import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import splitWise.BalanceSheet;
import splitWise.Expense;
import splitWise.Group;
import splitWise.User;
import splitWise.service.SplitService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SplitCalculatorTest {

    User alice;
    User bob;
    User charlie;
    Group trip;
    BalanceSheet balanceSheet;

    @BeforeEach
    public void setup() {
        alice = new User("u1", "Alice", "alice@email.com");
        bob = new User("u2", "Bob", "bob@email.com");
        charlie = new User("u3", "Charlie", "charlie@email.com");
        trip = new Group("g1", "Goa Trip");
        alice.joinGroup(trip);
        bob.joinGroup(trip);
        charlie.joinGroup(trip);

        balanceSheet = new BalanceSheet();
    }

    @Test
    public void expenseEqualTest() {
        List<User> allUsers = Arrays.asList(alice, bob, charlie);
        Expense e1 = SplitService.createEqualExpense("e1", "Hotel", 4500, alice, trip, allUsers, new Date());
        alice.addExpense(e1, balanceSheet);

        double amount = balanceSheet.getUserBalances(bob).get(alice);

        assertEquals(1500, amount, 0.01);
    }

    @Test
    public void expenseExactTest() {
        List<User> allUsers = Arrays.asList(alice, bob, charlie);
        List<Double> exacts = Arrays.asList(1200.0, 1500.0, 1300.0);
        Expense e1 = SplitService.createExactExpense("e1", "Dinner", 4000, alice, trip, exacts, allUsers, new Date());
        alice.addExpense(e1, balanceSheet);

        double amount = balanceSheet.getUserBalances(charlie).get(alice);

        assertEquals(1300, amount, 0.01);
    }

    @Test
    public void expensePercentTest() {
        List<User> allUsers = Arrays.asList(alice, bob, charlie);
        List<Double> percents = Arrays.asList(40.0, 20.0, 40.0);
        Expense e3 = SplitService.createPercentExpense("e3", "Scooter", 1200, charlie, trip, percents, allUsers, new Date());
        charlie.addExpense(e3, balanceSheet);

        double amount = balanceSheet.getUserBalances(alice).get(charlie);

        assertEquals(480, amount, 0.01);
    }
}
