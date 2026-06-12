package view;

import model.entity.ProductionJob;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ProductionView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");
    private final Scanner scanner;

    public ProductionView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayHeader() {
        System.out.println("\n================================================================");
        System.out.println("[5] 생산라인 조회   FIFO 방식");
        System.out.println("----------------------------------------------------------------");
        System.out.println("생산라인  1개  (단일 라인)    현재 상태:  [RUNNING]");
        System.out.println("----------------------------------------------------------------");
    }

    public void displayCurrentJob(ProductionJob job, LocalDateTime startTime, LocalDateTime expectedEnd) {
        System.out.println("현재 처리 중");
        System.out.println();
        if (job == null) {
            System.out.println("  현재 생산 중인 작업이 없습니다. (IDLE)");
            return;
        }
        System.out.printf("│ 주문번호   %-20s  시료   %s%n",
                job.getOrderId(), job.getSampleId());
        System.out.printf("│ 주문량     %dea    재고부족  %dea  →  실생산량  %dea%n",
                job.getOrderQty(), job.getShortage(), job.getActualQty());
        System.out.printf("│ 상태       [생산 중]");
        if (expectedEnd != null) {
            System.out.printf("    완료 예정  %s%n", expectedEnd.format(FMT));
        } else {
            System.out.println();
        }
    }

    public void displayWaitingQueue(List<ProductionJob> waiting, LocalDateTime baseTime) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("대기 중인 주문  (FIFO 순)");
        System.out.println();
        if (waiting.isEmpty()) {
            System.out.println("  대기 중인 주문이 없습니다.");
            return;
        }
        System.out.printf("  %-5s %-22s %-12s %8s %8s %8s%n",
                "순서", "주문번호", "시료", "주문량", "부족분", "실생산량");
        System.out.println("  " + "-".repeat(68));
        LocalDateTime cumTime = baseTime;
        for (int i = 0; i < waiting.size(); i++) {
            ProductionJob job = waiting.get(i);
            double minutes = job.getAvgProductionTime() * job.getActualQty();
            cumTime = cumTime.plusSeconds((long) (minutes * 60));
            System.out.printf("  %-5d %-22s %-12s %5dea  %5dea  %5dea    %s%n",
                    i + 1, job.getOrderId(), job.getSampleId(),
                    job.getOrderQty(),
                    job.getShortage(), job.getActualQty(), cumTime.format(FMT));
        }
        System.out.println();
        System.out.println("  * 부족분 = 주문량 - 재고,   실생산량 = ceil(부족분 / (수율 * 0.9))");
        System.out.println("  * 선입선출(FIFO) 방식으로 처리됩니다.");
    }

    public boolean askCompleteProduction() {
        System.out.println("----------------------------------------------------------------");
        System.out.print("현재 생산 완료 처리를 하시겠습니까? [Y/N]: ");
        return scanner.nextLine().trim().equalsIgnoreCase("Y");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
