package controller;

import view.MainMenuView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class MainController {

    private static final int MENU_MIN = 0;
    private static final int MENU_MAX = 6;

    private final MainMenuView mainMenuView;
    private final Map<Integer, Runnable> handlers;
    private final DashboardProvider dashboard;
    private final Scanner scanner;

    MainController(MainMenuView mainMenuView) {
        this(mainMenuView, Map.of(), null, new Scanner(System.in));
    }

    public MainController(MainMenuView mainMenuView,
                          Map<Integer, Runnable> handlers,
                          DashboardProvider dashboard,
                          Scanner scanner) {
        this.mainMenuView = mainMenuView;
        this.handlers = handlers;
        this.dashboard = dashboard;
        this.scanner = scanner;
    }

    public boolean isValidMenuChoice(int choice) {
        return choice >= MENU_MIN && choice <= MENU_MAX;
    }

    public void run() {
        while (true) {
            printDashboard();
            mainMenuView.displayMainMenu();
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                mainMenuView.displayError("숫자를 입력하세요.");
                continue;
            }
            if (!isValidMenuChoice(choice)) {
                mainMenuView.displayError("0~6 사이 번호를 입력하세요.");
                continue;
            }
            if (choice == 0) {
                mainMenuView.displayMessage("시스템을 종료합니다.");
                return;
            }
            Runnable handler = handlers.get(choice);
            if (handler != null) handler.run();
        }
    }

    private void printDashboard() {
        if (dashboard == null) return;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("\n========================================");
        System.out.println("  S-Semi 시료 생산주문관리 시스템");
        System.out.println("========================================");
        System.out.printf("  등록 시료: %d종  |  총 재고: %dea%n",
                dashboard.getSampleCount(), dashboard.getTotalStock());
        System.out.printf("  전체 주문: %d건  |  생산라인 대기: %d건%n",
                dashboard.getTotalOrderCount(), dashboard.getProductionQueueSize());
        System.out.println("  현재 일시: " + now);
        System.out.println("========================================");
    }
}
