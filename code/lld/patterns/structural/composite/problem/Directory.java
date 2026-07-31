import java.util.ArrayList;
import java.util.List;

// A composite - but with no shared abstraction, so it must hold Object and
// then ask what everything is.
public class Directory {
    String directoryName;
    List<Object> objectList;

    public Directory(String name) {
        this.directoryName = name;
        this.objectList = new ArrayList<>();
    }

    public void add(Object object) {
        objectList.add(object);
    }

    public void remove(Object object) {
        objectList.remove(object);
    }

    // Breaks OCP: every new node type adds another branch here, and to every
    // other method that walks the tree.
    public void printContents() {
        System.out.println("Directory Name: " + directoryName);
        for (Object obj : objectList) {
            if (obj instanceof MyFile) {
                ((MyFile) obj).printContents();
            } else if (obj instanceof Directory) {
                ((Directory) obj).printContents();
            }
        }
    }
}
