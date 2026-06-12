package view;

import model.entity.Sample;

import java.util.List;
import java.util.Scanner;

public class SampleView {

    private final Scanner scanner;

    public SampleView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displaySampleMenu() {
        System.out.println("\n=== [1] 시료 관리 ===");
        System.out.println("[1] 시료 등록");
        System.out.println("[2] 시료 조회");
        System.out.println("[3] 시료 검색");
        System.out.println("[0] 돌아가기");
        System.out.print("선택: ");
    }

    public Sample inputNewSample() {
        System.out.println("\n--- 시료 등록 ---");
        System.out.print("시료 ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("시료 이름: ");
        String name = scanner.nextLine().trim();
        System.out.print("평균 생산시간 (min/ea): ");
        double avgTime = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("수율 (0 초과 1 이하): ");
        double yield = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("초기 재고 (ea): ");
        int stock = Integer.parseInt(scanner.nextLine().trim());
        return new Sample(id, name, avgTime, yield, stock);
    }

    public void displaySampleList(List<Sample> samples) {
        if (samples.isEmpty()) {
            System.out.println("등록된 시료가 없습니다.");
            return;
        }
        System.out.printf("%-10s %-25s %10s %8s %8s%n", "ID", "이름", "생산시간", "수율", "재고");
        System.out.println("-".repeat(65));
        for (Sample s : samples) {
            System.out.printf("%-10s %-25s %8.2f분 %7.2f %7dea%n",
                    s.getId(), s.getName(), s.getAvgProductionTime(), s.getYield(), s.getStock());
        }
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }

    public String inputSearchKeyword() {
        System.out.print("검색어 (이름 또는 ID): ");
        return scanner.nextLine().trim();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
