package controller;

import view.MainMenuView;

public class MainController {

    private static final int MENU_MIN = 0;
    private static final int MENU_MAX = 6;

    private final MainMenuView view;

    public MainController(MainMenuView view) {
        this.view = view;
    }

    public boolean isValidMenuChoice(int choice) {
        return choice >= MENU_MIN && choice <= MENU_MAX;
    }
}
