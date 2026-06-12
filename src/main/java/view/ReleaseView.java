package view;

import model.entity.Order;

import java.util.List;
import java.util.Scanner;

public class ReleaseView {

    private final Scanner scanner;

    public ReleaseView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayConfirmedList(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("출고 가능한 주문이 없습니다.");
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
        System.out.print("출고할 주문 번호 (0=돌아가기): ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        if (idx == 0) return -1;
        return idx - 1;
    }

    public void displayReleaseResult(Order order) {
        System.out.println("\n출고 완료!");
        System.out.println("주문번호: " + order.getOrderId() + " → [" + order.getStatus() + "]");
        System.out.println("출고 수량: " + order.getQuantity() + "ea");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
