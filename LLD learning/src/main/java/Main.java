
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tictactoe.game.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class Main {
    static {
        System.out.println("In Static Block");
    }

    public static void main(String[] args) {
        System.out.println("In main");
    }
}

