package controller;

import model.entity.OrderStatus;
import model.service.*;
import view.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainController {

    private static final int MENU_MIN = 0;
    private static final int MENU_MAX = 6;

    private final MainMenuView mainMenuView;
    private final SampleController sampleController;
    private final OrderController orderController;
    private final ApprovalController approvalController;
    private final MonitoringController monitoringController;
    private final ProductionController productionController;
    private final ReleaseController releaseController;
    private final SampleService sampleService;
    private final OrderService orderService;
    private final ProductionQueue productionQueue;
    private final Scanner scanner;

    MainController(MainMenuView mainMenuView) {
        this(mainMenuView, null, null, null, null, null, null, null, null, null, null);
    }

    public MainController(MainMenuView mainMenuView,
                          SampleController sampleController,
                          OrderController orderController,
                          ApprovalController approvalController,
                          MonitoringController monitoringController,
                          ProductionController productionController,
                          ReleaseController releaseController,
                          SampleService sampleService,
                          OrderService orderService,
                          ProductionQueue productionQueue,
                          Scanner scanner) {
        this.mainMenuView = mainMenuView;
        this.sampleController = sampleController;
        this.orderController = orderController;
        this.approvalController = approvalController;
        this.monitoringController = monitoringController;
        this.productionController = productionController;
        this.releaseController = releaseController;
        this.sampleService = sampleService;
        this.orderService = orderService;
        this.productionQueue = productionQueue;
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
            switch (choice) {
                case 1 -> sampleController.run();
                case 2 -> orderController.run();
                case 3 -> approvalController.run();
                case 4 -> monitoringController.run();
                case 5 -> productionController.run();
                case 6 -> releaseController.run();
                case 0 -> {
                    mainMenuView.displayMessage("시스템을 종료합니다.");
                    return;
                }
            }
        }
    }

    private void printDashboard() {
        long totalStock = sampleService.findAll().stream().mapToLong(s -> s.getStock()).sum();
        long totalOrders = orderService.findAll().size();
        int queueSize = productionQueue.size();
        int sampleCount = sampleService.findAll().size();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.println("\n========================================");
        System.out.println("  S-Semi 시료 생산주문관리 시스템");
        System.out.println("========================================");
        System.out.printf("  등록 시료: %d종  |  총 재고: %dea%n", sampleCount, totalStock);
        System.out.printf("  전체 주문: %d건  |  생산라인 대기: %d건%n", totalOrders, queueSize);
        System.out.println("  현재 일시: " + now);
        System.out.println("========================================");
    }
}
