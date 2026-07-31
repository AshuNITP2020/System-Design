import java.util.List;

// Product: built only through a builder, so it can be constructed in one shot
// and then left alone.
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

    // Package-private: only a builder in this package can call it.
    Student(StudentBuilder builder) {
        this.rollNumber = builder.rollNumber;
        this.age = builder.age;
        this.name = builder.name;
        this.branch = builder.branch;
        this.fatherName = builder.fatherName;
        this.motherName = builder.motherName;
        this.subjects = builder.subjects;
        this.mobileNo = builder.mobileNo;
        this.emailId = builder.emailId;
    }

    @Override
    public String toString() {
        return " roll number: " + rollNumber
                + " age: " + age
                + " name: " + name
                + " branch: " + branch
                + " father name: " + fatherName
                + " mother name: " + motherName
                + " subjects: " + subjects
                + " mobile no: " + mobileNo
                + " email id: " + emailId;
    }
}
