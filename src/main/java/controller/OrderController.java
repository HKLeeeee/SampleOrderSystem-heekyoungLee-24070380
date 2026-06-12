package controller;

import model.entity.Sample;
import model.service.OrderService;
import model.service.SampleService;
import view.OrderView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class OrderController {

    private final OrderService orderService;
    private final SampleService sampleService;
    private final OrderView view;

    public OrderController(OrderService orderService, SampleService sampleService, OrderView view) {
        this.orderService = orderService;
        this.sampleService = sampleService;
        this.view = view;
    }

    public void run() {
        try {
            String[] info = view.inputOrderInfo();
            String sampleId = info[0];
            String customerName = info[1];
            int quantity = Integer.parseInt(info[2]);

            Optional<Sample> sampleOpt = sampleService.findAll().stream()
                    .filter(s -> s.getId().equals(sampleId))
                    .findFirst();

            if (sampleOpt.isEmpty()) {
                view.displayMessage("[오류] 등록되지 않은 시료 ID입니다. [1] 시료 관리에서 먼저 등록하세요.");
                return;
            }

            Sample sample = sampleOpt.get();
            boolean confirmed = view.confirmOrder(sampleId, sample.getName(), customerName, quantity);
            if (!confirmed) {
                view.displayMessage("주문이 취소되었습니다.");
                return;
            }

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            var order = orderService.placeOrder(sampleId, customerName, quantity, date);
            view.displayOrderResult(order);
        } catch (NumberFormatException e) {
            view.displayMessage("[오류] 수량은 숫자로 입력하세요.");
        } catch (Exception e) {
            view.displayMessage("[오류] " + e.getMessage());
        }
    }
}
