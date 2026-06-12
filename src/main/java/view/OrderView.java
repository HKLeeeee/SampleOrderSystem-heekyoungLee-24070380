package view;

import model.entity.Order;
import model.entity.Sample;

import java.util.List;
import java.util.Scanner;

public class OrderView {

    private final Scanner scanner;

    public OrderView(Scanner scanner) {
        this.scanner = scanner;
    }

    public String[] inputOrderInfo() {
        System.out.println("\n--- 시료 주문 접수 ---");
        System.out.print("시료 ID: ");
        String sampleId = scanner.nextLine().trim();
        System.out.print("고객명: ");
        String customerName = scanner.nextLine().trim();
        System.out.print("주문 수량 (ea): ");
        String quantity = scanner.nextLine().trim();
        return new String[]{sampleId, customerName, quantity};
    }

    public boolean confirmOrder(String sampleId, String sampleName, String customerName, int quantity) {
        System.out.println("\n--- 주문 내용 확인 ---");
        System.out.println("시료: " + sampleId + " (" + sampleName + ")");
        System.out.println("고객: " + customerName);
        System.out.println("수량: " + quantity + "ea");
        System.out.println("※ 재고 확인은 [3] 승인 메뉴에서 진행됩니다.");
        System.out.print("[Y] 예약 접수 / [N] 취소: ");
        return scanner.nextLine().trim().equalsIgnoreCase("Y");
    }

    public void displayOrderResult(Order order) {
        System.out.println("\n주문 접수 완료!");
        System.out.println("주문번호: " + order.getOrderId() + " [" + order.getStatus() + "]");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
