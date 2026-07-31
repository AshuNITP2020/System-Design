// A leaf. Note: named MyFile only to avoid confusion with java.io.File.
public class MyFile {
    String fileName;

    public MyFile(String name) {
        this.fileName = name;
    }

    public void printContents() {
        System.out.println("File name: " + fileName);
    }
}
