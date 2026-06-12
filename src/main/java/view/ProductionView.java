package view;

import model.entity.ProductionJob;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ProductionView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private final Scanner scanner;

    public ProductionView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayCurrentJob(ProductionJob job, LocalDateTime startTime, LocalDateTime expectedEnd) {
        if (job == null) {
            System.out.println("현재 생산 중인 작업이 없습니다. (IDLE)");
            return;
        }
        System.out.println("\n--- 현재 생산 중 ---");
        System.out.println("주문번호: " + job.getOrderId() + "  시료: " + job.getSampleId());
        System.out.println("부족분: " + job.getShortage() + "ea  실생산량: " + job.getActualQty() + "ea");
        if (expectedEnd != null) {
            System.out.println("예상 완료: " + expectedEnd.format(FMT));
        }
    }

    public void displayWaitingQueue(List<ProductionJob> waiting, LocalDateTime baseTime) {
        System.out.println("\n--- 생산 대기 큐 (FIFO) ---");
        if (waiting.isEmpty()) {
            System.out.println("대기 중인 주문이 없습니다.");
            return;
        }
        System.out.printf("%-5s %-22s %-10s %8s %8s %12s%n", "순서", "주문번호", "시료", "부족분", "실생산량", "예상완료");
        System.out.println("-".repeat(70));
        LocalDateTime cumTime = baseTime;
        for (int i = 0; i < waiting.size(); i++) {
            ProductionJob job = waiting.get(i);
            double minutes = job.getAvgProductionTime() * job.getActualQty();
            cumTime = cumTime.plusSeconds((long) (minutes * 60));
            System.out.printf("%-5d %-22s %-10s %6dea %6dea %12s%n",
                    i + 1, job.getOrderId(), job.getSampleId(),
                    job.getShortage(), job.getActualQty(), cumTime.format(FMT));
        }
    }

    public boolean askCompleteProduction() {
        System.out.print("\n현재 생산 완료 처리를 하시겠습니까? [Y/N]: ");
        return scanner.nextLine().trim().equalsIgnoreCase("Y");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
