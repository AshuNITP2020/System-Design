// Leaf: no children.
public class MyFile implements FileSystemComponent {
    String fileName;

    public MyFile(String name) {
        this.fileName = name;
    }

    @Override
    public void printContents() {
        System.out.println("File name: " + fileName);
    }
}
