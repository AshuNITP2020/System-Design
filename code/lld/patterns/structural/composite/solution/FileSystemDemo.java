public class FileSystemDemo {
    public static void main(String[] args) {
        System.out.println("======= Composite Design Pattern ======");

        MyFile receipt = new MyFile("receipt.pdf");
        MyFile invoice = new MyFile("invoice.pdf");
        MyFile torrentLinks = new MyFile("torrentLinks.txt");
        MyFile tomCruise = new MyFile("tomCruise.jpg");
        MyFile dumbAndDumber = new MyFile("DumbAndDumber.mp4");
        MyFile hangoverI = new MyFile("HangoverI.mp4");

        Directory moviesDirectory = new Directory("Movies");
        Directory comedyMovieDirectory = new Directory("ComedyMovies");

        moviesDirectory.add(receipt);
        moviesDirectory.add(invoice);
        moviesDirectory.add(torrentLinks);
        moviesDirectory.add(tomCruise);
        moviesDirectory.add(comedyMovieDirectory);
        comedyMovieDirectory.add(dumbAndDumber);
        comedyMovieDirectory.add(hangoverI);

        moviesDirectory.printContents();
    }
}
