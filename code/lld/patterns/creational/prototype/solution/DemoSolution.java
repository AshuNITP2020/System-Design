public class DemoSolution {
    public static void main(String[] args) {
        // Creating the first instance is the expensive part
        Student student = new Student(5, "Rita", "CSE", 224);
        student.printDetails();

        // Cloning is cheap - and rollNo comes along, private or not
        Student studentClone = (Student) student.clone();
        studentClone.setInHighSchool(true);
        studentClone.printDetails();

        System.out.println("Same object? " + (student == studentClone));   // false
    }
}
