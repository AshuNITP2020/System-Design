
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tictactoe.game.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        Map<User, String> map = new HashMap<>();

        User u1 = new User(1);
        User u2 = new User(1);
        User u3 = new User(1);
        System.out.println(u1.hashCode());
        System.out.println(u2.hashCode());

        map.put(u1, "First");
        map.put(u2, "Second");

        System.out.println(map.size());        // 2
        System.out.println(map.get(u1));       // First
        System.out.println(map.get(u2));       // Second
        System.out.println(map.get(u3));

        System.out.println(AppConfig.ENVIRONMENT);

        AppConfig.changeEnvironment("PROD");

        System.out.println(AppConfig.ENVIRONMENT);

    }
}

class AppConfig {

    // static variable (class-level)
    public static String ENVIRONMENT = "DEV";

    public static void changeEnvironment(String env) {
        ENVIRONMENT = env;
    }

}

class User {
    int id;

    User(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }
}

