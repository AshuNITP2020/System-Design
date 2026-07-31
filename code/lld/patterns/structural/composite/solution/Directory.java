import java.util.ArrayList;
import java.util.List;

// Composite: has children, and its children are Components - which may
// themselves be directories. That self-reference is what makes it a tree.
public class Directory implements FileSystemComponent {

    String directoryName;
    List<FileSystemComponent> children;

    public Directory(String name) {
        this.directoryName = name;
        this.children = new ArrayList<>();
    }

    public void add(FileSystemComponent fileSystemComponent) {
        children.add(fileSystemComponent);
    }

    public void remove(FileSystemComponent fileSystemComponent) {
        children.remove(fileSystemComponent);
    }

    @Override
    public void printContents() {
        System.out.println("Directory Name: " + directoryName);
        for (FileSystemComponent child : children) {
            child.printContents();      // no instanceof, no cast
        }
    }
}
