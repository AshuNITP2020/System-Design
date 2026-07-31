public class EmployeeManagement {
    public static void main(String[] args) {
        System.out.println("===== Proxy Design Pattern =====");

        // The client declares the SUBJECT type, not the proxy type
        EmployeeDao userProxyObj = new EmployeeDaoProxy("USER");
        userProxyObj.getEmployeeInfo(101);                      // allowed

        try {
            userProxyObj.createEmployee(new EmployeeDo(102, "Riya"));
        } catch (RuntimeException e) {
            System.out.println("USER  -> createEmployee: " + e.getMessage());
        }

        EmployeeDao adminProxyObj = new EmployeeDaoProxy("ADMIN");
        adminProxyObj.createEmployee(new EmployeeDo(102, "Riya"));   // allowed

        EmployeeDao guestProxyObj = new EmployeeDaoProxy("GUEST");
        try {
            guestProxyObj.getEmployeeInfo(101);
        } catch (RuntimeException e) {
            System.out.println("GUEST -> getEmployeeInfo: " + e.getMessage());
        }
    }
}
