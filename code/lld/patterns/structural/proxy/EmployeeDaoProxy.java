// Proxy: same interface, wraps the real subject, adds access control.
// Lazy loading, logging, caching and metrics all slot in at exactly this point
// without the real subject or the client changing at all.
public class EmployeeDaoProxy implements EmployeeDao {

    private final EmployeeDao empDaoObj;
    private final String clientRole;

    public EmployeeDaoProxy(String clientRole) {
        this.empDaoObj = new EmployeeDaoImpl();
        this.clientRole = clientRole;
    }

    @Override
    public void getEmployeeInfo(int empID) {
        if (clientRole.equals("ADMIN") || clientRole.equals("USER")) {
            empDaoObj.getEmployeeInfo(empID);
        } else {
            throw new RuntimeException("Access Denied");
        }
    }

    @Override
    public void createEmployee(EmployeeDo obj) {
        if (clientRole.equals("ADMIN")) {
            empDaoObj.createEmployee(obj);
        } else {
            throw new RuntimeException("Access Denied");
        }
    }
}
