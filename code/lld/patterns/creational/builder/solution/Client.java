public class Client {
    public static void main(String[] args) {
        System.out.println("===== Builder Pattern =====");

        StudentRegistrationDirector enggStudentDirector =
                new StudentRegistrationDirector(new EngineeringStudentBuilder());
        StudentRegistrationDirector mbaStudentDirector =
                new StudentRegistrationDirector(new MBAStudentBuilder());

        Student engineerStudent = enggStudentDirector.createStudent();
        Student mbaStudent = mbaStudentDirector.createStudent();

        System.out.println("===> Student details:" + engineerStudent);
        System.out.println("===> Student details:" + mbaStudent);

        // You can also skip the director and chain the steps yourself -
        // every argument is now labelled by its setter name.
        Student custom = new EngineeringStudentBuilder()
                .setRollNumber(3)
                .setAge(21)
                .setName("Ravi")
                .setBranch("ECE")
                .setEmailId("ravi@iitb.com")
                .setSubjects()
                .build();
        System.out.println("===> Student details:" + custom);
    }
}
