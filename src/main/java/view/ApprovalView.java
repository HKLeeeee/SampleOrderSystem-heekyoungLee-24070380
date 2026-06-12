package view;

import model.entity.Order;
import model.entity.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ApprovalView {

    private final Scanner scanner;

    public ApprovalView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayReservedList(List<Order> orders, Map<String, String> sampleNames) {
        System.out.println("\n================================================================");
        System.out.println("[3] 주문 승인/거절");
        System.out.println("----------------------------------------------------------------");
        if (orders.isEmpty()) {
            System.out.println("승인 대기 중인 예약 목록이 없습니다.  (RESERVED)");
            return;
        }
        System.out.println("승인 대기 중인 예약 목록  (RESERVED)");
        System.out.println();
        System.out.printf("%-6s %-22s %-14s %-22s %8s    %s%n",
                "번호", "주문번호", "고객", "시료", "수량", "상태");
        System.out.println("-".repeat(85));
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String name = sampleNames.getOrDefault(o.getSampleId(), o.getSampleId());
            System.out.printf("[%d]    %-22s %-14s %-22s %5dea    [%s]%n",
                    i + 1, o.getOrderId(), o.getCustomerName(), name,
                    o.getQuantity(), o.getStatus());
        }
    }

    public int selectOrderIndex(int size) {
        System.out.print("승인할 번호 > ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx == 0) return -1;
            return idx - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String selectAction() {
        System.out.print("[1] 승인   [2] 거절\n선택 > ");
        return scanner.nextLine().trim();
    }

    public boolean confirmProductionInfo(String sampleName, int currentStock,
                                         int shortage, int actualQty, double totalTime) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("재고 확인 중...");
        System.out.println();
        System.out.printf("  시료        %-20s  현재 재고  %dea%n", sampleName, currentStock);
        System.out.printf("  주문 수량   %-5dea              부족분     %dea  ← 이 수량만 생산%n",
                currentStock + shortage, shortage);
        System.out.println();
        System.out.printf("  재고 부족.  부족분 %dea 승인하시겠습니까?  (실생산량 %dea / %.0fmin)%n",
                shortage, actualQty, totalTime);
        System.out.println();
        System.out.print("[Y] 승인   [N] 주문 거절\n선택 > ");
        return scanner.nextLine().trim().equalsIgnoreCase("Y");
    }

    public void displayApprovalResult(String orderId, OrderStatus from, OrderStatus to) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("승인 완료.");
        System.out.println();
        System.out.printf("  상태 변경    %s  →  [%s]%n", from, to);
        System.out.printf("  주문번호     %s%n", orderId);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
