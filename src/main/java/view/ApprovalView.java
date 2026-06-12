package view;

import model.entity.Order;

import java.util.List;
import java.util.Scanner;

public class ApprovalView {

    private final Scanner scanner;

    public ApprovalView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayReservedList(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("승인 대기 주문이 없습니다.");
            return;
        }
        System.out.printf("%-5s %-22s %-12s %-10s %8s%n", "No", "주문번호", "고객", "시료ID", "수량");
        System.out.println("-".repeat(62));
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.printf("%-5d %-22s %-12s %-10s %6dea%n",
                    i + 1, o.getOrderId(), o.getCustomerName(), o.getSampleId(), o.getQuantity());
        }
    }

    public int selectOrderIndex(int size) {
        System.out.print("처리할 주문 번호 (0=돌아가기): ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        if (idx == 0) return -1;
        return idx - 1;
    }

    public String selectAction() {
        System.out.println("[1] 승인  [2] 거절");
        System.out.print("선택: ");
        return scanner.nextLine().trim();
    }

    public boolean confirmProductionInfo(int shortage, int actualQty, double totalTime) {
        System.out.println("\n재고 부족 — 생산이 필요합니다.");
        System.out.println("  부족분: " + shortage + "ea");
        System.out.println("  실생산량: " + actualQty + "ea");
        System.out.printf("  예상 생산시간: %.1f분%n", totalTime);
        System.out.print("[Y] 승인(생산 큐 등록) / [N] 거절: ");
        return scanner.nextLine().trim().equalsIgnoreCase("Y");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
