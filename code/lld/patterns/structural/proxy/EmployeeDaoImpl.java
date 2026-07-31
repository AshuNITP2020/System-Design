// Real Subject: the actual business logic and data access.
public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public void getEmployeeInfo(int empID) {
        System.out.println("Fetching employee info for ID: " + empID);
    }

    @Override
    public void createEmployee(EmployeeDo obj) {
        System.out.println("Creating employee: " + obj);
    }
}
