package view;

public class MainMenuView {

    public String buildMainMenuText() {
        return """
                ========================================
                  S-Semi 시료 생산주문관리 시스템
                ========================================
                [1] 시료 관리
                [2] 시료 주문 (예약 접수)
                [3] 주문 승인/거절
                [4] 모니터링
                [5] 생산라인 조회
                [6] 출고 처리
                [0] 종료
                ========================================
                선택: """;
    }

    public void displayMainMenu() {
        System.out.print(buildMainMenuText());
    }

    public void displayError(String message) {
        System.out.println("[오류] " + message);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
