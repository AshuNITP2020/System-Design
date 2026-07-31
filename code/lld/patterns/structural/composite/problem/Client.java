public class Client {
    public static void main(String[] args) {
        System.out.println("===== Composite: the problem =====");

        Directory movieDirectory = new Directory("Movies");
        movieDirectory.add(new MyFile("RentalReceipt"));

        Directory comedyMovieDirectory = new Directory("ComedyMovies");
        comedyMovieDirectory.add(new MyFile("DumbAndDumber"));
        movieDirectory.add(comedyMovieDirectory);

        movieDirectory.printContents();

        // Add a ZipFolder or a Shortcut and every instanceof chain in the
        // codebase needs another branch.
    }
}
