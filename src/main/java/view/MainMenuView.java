package view;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainMenuView {

    private static final String LOGO =
            "███████╗      ███████╗███████╗███╗   ███╗██╗\n" +
            "██╔════╝      ██╔════╝██╔════╝████╗ ████║██║\n" +
            "███████╗█████╗███████╗█████╗  ██╔████╔██║██║\n" +
            "╚════██║╚════╝╚════██║██╔══╝  ██║╚██╔╝██║██║\n" +
            "███████║      ███████║███████╗██║ ╚═╝ ██║██║\n" +
            "╚══════╝      ╚══════╝╚══════╝╚═╝     ╚═╝╚═╝";

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final NumberFormat NUM_FMT = NumberFormat.getNumberInstance(Locale.KOREA);

    public void displayDashboard(int sampleCount, long totalStock,
                                 long totalOrderCount, int queueSize) {
        String now = LocalDateTime.now().format(DT_FMT);
        System.out.println();
        System.out.println(LOGO);
        System.out.println("반도체 시료 생산주문관리 시스템");
        System.out.println("================================================================");
        System.out.println();
        System.out.printf("  시스템 현황  %s%n%n", now);
        System.out.printf("  등록 시료  %3d종      총 재고  %8s ea%n",
                sampleCount, NUM_FMT.format(totalStock));
        System.out.printf("  전체 주문  %3d건      생산라인 %3d건 대기%n",
                totalOrderCount, queueSize);
        System.out.println("----------------------------------------------------------------");
    }

    public void displayMainMenu() {
        System.out.println("[1] 시료 관리                    [2] 시료 주문");
        System.out.println("[3] 주문 승인/거절               [4] 모니터링");
        System.out.println("[5] 생산라인 조회                [6] 출고 처리");
        System.out.println("[0] 종료");
        System.out.println("----------------------------------------------------------------");
        System.out.print("선택 > ");
    }

    public void displayError(String message) {
        System.out.println("[오류] " + message);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
