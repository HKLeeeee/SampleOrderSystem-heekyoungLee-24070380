package view;

import model.entity.OrderStatus;
import model.entity.Sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MonitoringView {

    private static final int BAR_WIDTH = 10;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Scanner scanner;

    public MonitoringView() {
        this.scanner = null;
    }

    public MonitoringView(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readSubMenu() {
        String now = LocalDateTime.now().format(DT_FMT);
        System.out.println("\n================================================================");
        System.out.printf("[4] 모니터링   %s%n", now);
        System.out.println("----------------------------------------------------------------");
        System.out.print("[1] 주문량 확인   [2] 재고량 확인   [0] 뒤로\n선택 > ");
        return scanner != null ? scanner.nextLine().trim() : "0";
    }

    public void displayOrderCounts(Map<OrderStatus, Long> counts) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("상태별 주문 현황");
        System.out.printf("  [%-12s]  %d건%n", "RESERVED",  counts.getOrDefault(OrderStatus.RESERVED,  0L));
        System.out.printf("  [%-12s]  %d건%n", "CONFIRMED", counts.getOrDefault(OrderStatus.CONFIRMED, 0L));
        System.out.printf("  [%-12s]  %d건   ← 생산라인 대기%n", "PRODUCING", counts.getOrDefault(OrderStatus.PRODUCING, 0L));
        System.out.printf("  [%-12s]  %d건%n", "RELEASE",   counts.getOrDefault(OrderStatus.RELEASE,   0L));
    }

    public record StockRow(String name, int stock, String status, int ratio) {}

    public void displayStockStatus(List<StockRow> rows) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("재고 현황");
        System.out.println();
        if (rows.isEmpty()) {
            System.out.println("등록된 시료가 없습니다.");
            return;
        }
        System.out.printf("  %-24s  %-10s  %-6s  %s%n", "시료명", "재고", "상태", "잔여율");
        System.out.println("  " + "-".repeat(60));
        for (StockRow row : rows) {
            String bar = buildBar(row.ratio());
            System.out.printf("  %-24s  %5dea    [%-4s]  %s %3d%%%n",
                    row.name(), row.stock(), row.status(), bar, row.ratio());
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    private String buildBar(int pct) {
        int filled = (int) Math.round(pct / 100.0 * BAR_WIDTH);
        filled = Math.max(0, Math.min(BAR_WIDTH, filled));
        return "█".repeat(filled) + "─".repeat(BAR_WIDTH - filled);
    }
}
