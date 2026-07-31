import java.util.HashMap;
import java.util.Map;

// Flyweight Factory: one DocumentCharacter per distinct character.
public class LetterFactory {

    private static final Map<Character, ILetter> characterCache = new HashMap<>();

    public static ILetter createLetter(char characterValue) {
        if (characterCache.containsKey(characterValue)) {
            return characterCache.get(characterValue);
        }
        DocumentCharacter characterObj = new DocumentCharacter(characterValue, "Arial", 10);
        characterCache.put(characterValue, characterObj);
        return characterObj;
    }

    public static int getTotalCharacters() {
        return characterCache.size();
    }
}
