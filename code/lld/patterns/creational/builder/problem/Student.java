import java.util.List;

// The telescoping-constructor problem: four mandatory fields, five optional
// ones, and a constructor for every combination anyone has needed so far.
public class Student {

    // mandatory
    int rollNumber;
    int age;
    String name;
    String branch;
    // optional
    String fatherName;
    String motherName;
    List<String> subjects;
    String mobileNo;
    String emailId;

    public Student(int rollNumber, int age, String name, String branch) {
        this.rollNumber = rollNumber;
        this.age = age;
        this.name = name;
        this.branch = branch;
    }

    // + fatherName
    public Student(int rollNumber, int age, String name, String branch, String fatherName) {
        this(rollNumber, age, name, branch);
        this.fatherName = fatherName;
    }

    // + motherName
    public Student(int rollNumber, int age, String name, String branch,
                   String fatherName, String motherName) {
        this(rollNumber, age, name, branch, fatherName);
        this.motherName = motherName;
    }

    // + emailId
    public Student(int rollNumber, int age, String name, String branch,
                   String fatherName, String motherName, String emailId) {
        this(rollNumber, age, name, branch, fatherName, motherName);
        this.emailId = emailId;
    }

    // + mobileNo -- CANNOT BE WRITTEN. Identical signature to the constructor
    // above: (int, int, String, String, String, String, String). Uncomment and
    // this file stops compiling. That is the wall the pattern exists to avoid.
    /*
    public Student(int rollNumber, int age, String name, String branch,
                   String fatherName, String motherName, String mobileNo) {
        this(rollNumber, age, name, branch, fatherName, motherName);
        this.mobileNo = mobileNo;
    }
    */

    // The all-arguments constructor: which String is which at the call site?
    public Student(int rollNumber, int age, String name, String branch,
                   String fatherName, String motherName, List<String> subjects,
                   String mobileNo, String emailId) {
        this(rollNumber, age, name, branch, fatherName, motherName, emailId);
        this.subjects = subjects;
        this.mobileNo = mobileNo;
    }

    public void printDetails() {
        System.out.println("=== Student Details ===");
        System.out.println("Roll No: " + rollNumber + ", Name: " + name + ", Age: " + age
                + ", Branch: " + branch + ", Father: " + fatherName + ", Mother: " + motherName
                + ", Subjects: " + subjects + ", Mobile: " + mobileNo + ", Email: " + emailId);
    }
}
