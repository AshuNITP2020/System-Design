// Component: the one type the client programs against. Leaf and composite
// both implement it, so the client stops caring which is which.
public interface FileSystemComponent {
    void printContents();
}
