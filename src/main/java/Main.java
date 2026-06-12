import java.util.Scanner;

public class Main {

    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        AppContext ctx = new AppContext(DATA_DIR);
        ctx.buildMainController(new Scanner(System.in)).run();
    }
}
