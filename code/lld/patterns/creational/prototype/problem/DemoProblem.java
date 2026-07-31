// The client tries to clone by copying fields itself.
public class DemoProblem {
    public static void main(String[] args) {
        Student studentOrg = new Student(1, "Aman", "CSE", 123);
        studentOrg.printDetails();

        // Copy the object field by field, from outside the class
        Student studentClone = new Student();
        studentClone.id = studentOrg.id;
        studentClone.name = studentOrg.name;
        studentClone.branch = studentOrg.branch;
        // studentClone.rollNo = studentOrg.rollNo;   // compilation error - rollNo is private

        System.out.println("Clone is missing rollNo:");
        studentClone.printDetails();
    }
}
