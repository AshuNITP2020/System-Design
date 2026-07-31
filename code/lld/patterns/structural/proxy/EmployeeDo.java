// Plain data object passed around by the DAO.
public class EmployeeDo {
    int id;
    String name;

    public EmployeeDo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Employee{" + id + ", " + name + "}";
    }
}
