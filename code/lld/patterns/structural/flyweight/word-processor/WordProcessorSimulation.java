public class WordProcessorSimulation {
    public static void main(String[] args) {
        System.out.println("====== Flyweight: word processor ======");

        String text = "Hello World";     // 11 characters, 8 distinct

        for (int i = 0; i < text.length(); i++) {
            ILetter letter = LetterFactory.createLetter(text.charAt(i));
            letter.display(0, i);
        }

        System.out.println("Characters rendered: " + text.length());
        System.out.println("Objects actually created: " + LetterFactory.getTotalCharacters());
        // 'l' appears 3 times and 'o' twice, but each exists once in memory.
        // Scale to a real document of millions of characters and the saving is
        // the difference between running and crashing.
    }
}
