// Subject: the shared interface. Because the proxy implements it too, the
// client cannot tell which one it is holding.
public interface EmployeeDao {
    void getEmployeeInfo(int empID);
    void createEmployee(EmployeeDo obj);
}
