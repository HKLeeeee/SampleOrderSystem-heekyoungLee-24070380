package view;

import model.entity.OrderStatus;
import model.entity.Sample;

import java.util.List;
import java.util.Map;

public class MonitoringView {

    public void displayOrderCounts(Map<OrderStatus, Long> counts) {
        System.out.println("\n--- 주문량 현황 (REJECTED 제외) ---");
        for (OrderStatus status : new OrderStatus[]{
                OrderStatus.RESERVED, OrderStatus.PRODUCING, OrderStatus.CONFIRMED, OrderStatus.RELEASE}) {
            System.out.printf("  %-12s: %d건%n", status, counts.getOrDefault(status, 0L));
        }
    }

    public void displayStockStatus(List<Sample> samples, java.util.function.Function<Sample, String> statusFn) {
        System.out.println("\n--- 재고량 현황 ---");
        if (samples.isEmpty()) {
            System.out.println("등록된 시료가 없습니다.");
            return;
        }
        System.out.printf("%-10s %-25s %8s %6s%n", "ID", "이름", "재고", "상태");
        System.out.println("-".repeat(53));
        for (Sample s : samples) {
            System.out.printf("%-10s %-25s %6dea %6s%n",
                    s.getId(), s.getName(), s.getStock(), statusFn.apply(s));
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
