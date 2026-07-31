import java.util.List;

public class DemoProblem {
    public static void main(String[] args) {
        System.out.println("===== Builder: the problem =====");

        // Readable enough
        Student a = new Student(1, 22, "John", "CSE");
        a.printDetails();

        // Nine positional arguments, five of them String. Swap two by accident
        // and it still compiles and still runs - just with wrong data.
        Student b = new Student(2, 24, "Sarah", "MBA", "Gabriel", "Taylor",
                List.of("Micro Economics", "Business Studies"),
                "9876543210", "sarah@iitb.com");
        b.printDetails();

        // Want only rollNumber + email? Pass nulls for everything in between.
        Student c = new Student(3, 21, "Ravi", "ECE", null, null, "ravi@iitb.com");
        c.printDetails();
    }
}
