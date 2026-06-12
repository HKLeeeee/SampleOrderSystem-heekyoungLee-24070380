import controller.MainController;
import view.MainMenuView;

public class Main {

    public static void main(String[] args) {
        MainMenuView view = new MainMenuView();
        MainController controller = new MainController(view);
        view.displayMainMenu();
    }
}
