// Concrete class whose clone the client wants to create.
public class Student {

    int id;
    String name;
    String branch;
    private int rollNo;   // <-- private: the client cannot copy this

    Student() {
    }

    Student(int id, String name, String branch, int rollNo) {
        this.id = id;
        this.name = name;
        this.branch = branch;
        this.rollNo = rollNo;
    }

    public void printDetails() {
        System.out.println("=== Student Details ===");
        System.out.print(this + ": ");
        System.out.println("Id: " + id + ", Name: " + name
                + ", Branch: " + branch + ", Roll No: " + rollNo);
    }
}
