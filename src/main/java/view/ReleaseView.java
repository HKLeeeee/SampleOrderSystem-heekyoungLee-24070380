package view;

import model.entity.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ReleaseView {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Scanner scanner;

    public ReleaseView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayConfirmedList(List<Order> orders, Map<String, String> sampleNames) {
        System.out.println("\n================================================================");
        System.out.println("[6] 출고 처리");
        System.out.println("----------------------------------------------------------------");
        if (orders.isEmpty()) {
            System.out.println("출고 가능 주문이 없습니다.  (CONFIRMED)");
            return;
        }
        System.out.println("출고 가능 주문  (CONFIRMED)");
        System.out.println();
        System.out.printf("%-6s %-22s %-14s %-22s %8s%n", "번호", "주문번호", "고객", "시료", "수량");
        System.out.println("-".repeat(80));
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String name = sampleNames.getOrDefault(o.getSampleId(), o.getSampleId());
            System.out.printf("[%d]    %-22s %-14s %-22s %5dea%n",
                    i + 1, o.getOrderId(), o.getCustomerName(), name, o.getQuantity());
        }
    }

    public int selectOrderIndex(int size) {
        System.out.print("출고할 번호 > ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx == 0) return -1;
            return idx - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void displayReleaseResult(Order order, LocalDateTime processedAt) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("출고 처리 완료.");
        System.out.println();
        System.out.printf("  주문번호    %s%n", order.getOrderId());
        System.out.printf("  출고수량    %dea%n", order.getQuantity());
        System.out.printf("  처리일시    %s%n", processedAt.format(DT_FMT));
        System.out.printf("  상태        CONFIRMED  →  [%s]%n", order.getStatus());
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
