package view;

import model.entity.Sample;

import java.util.List;
import java.util.Scanner;

public class SampleView {

    public record SampleInput(String id, String name, double avgProductionTime, double yield, int stock) {}

    private static final int PAGE_SIZE = 5;

    private final Scanner scanner;

    public SampleView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displaySampleMenu() {
        System.out.println("\n================================================================");
        System.out.println("[1] 시료 관리");
        System.out.println("----------------------------------------------------------------");
        System.out.print("[1] 시료 등록   [2] 시료 목록   [3] 시료 검색   [0] 뒤로\n선택 > ");
    }

    public SampleInput inputNewSample() {
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
        return new SampleInput(id, name, avgTime, yield, stock);
    }

    public void displaySampleList(List<Sample> samples) {
        if (samples.isEmpty()) {
            System.out.println("등록된 시료가 없습니다.");
            return;
        }
        int totalPages = (int) Math.ceil((double) samples.size() / PAGE_SIZE);
        int page = 0;

        while (true) {
            int from = page * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, samples.size());
            System.out.printf("%n등록 시료 목록  (총 %d종)%n%n", samples.size());
            System.out.printf("%-8s %-24s %-16s %-8s %8s%n", "ID", "시료명", "평균 생산시간", "수율", "현재 재고");
            System.out.println("-".repeat(70));
            for (int i = from; i < to; i++) {
                Sample s = samples.get(i);
                System.out.printf("%-8s %-24s %-16s %-8.2f %6dea%n",
                        s.getId(), s.getName(), formatTime(s.getAvgProductionTime()),
                        s.getYield(), s.getStock());
            }
            System.out.println("-".repeat(70));

            boolean hasPrev = page > 0;
            boolean hasNext = page < totalPages - 1;

            if (!hasNext && !hasPrev) break;

            StringBuilder nav = new StringBuilder();
            if (hasPrev) nav.append("[P] 이전페이지  ");
            if (hasNext) nav.append("...외 ").append(samples.size() - to).append("종  [N] 다음페이지  ");
            nav.append("[0] 돌아가기");
            System.out.print(nav + "\n선택 > ");

            String input = scanner.nextLine().trim().toUpperCase();
            if ("N".equals(input) && hasNext) page++;
            else if ("P".equals(input) && hasPrev) page--;
            else break;
        }
    }

    private static String formatTime(double avgTime) {
        if (avgTime == Math.floor(avgTime)) return (int) avgTime + " min/ea";
        String s = String.valueOf(avgTime);
        return s.replaceAll("0+$", "").replaceAll("\\.$", "") + " min/ea";
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
