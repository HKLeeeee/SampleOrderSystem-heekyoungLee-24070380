package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.service.ApprovalService;
import model.service.OrderService;
import model.service.ProductionCalculator;
import model.service.SampleService;
import view.ApprovalView;

import java.util.List;
import java.util.Optional;

public class ApprovalController {

    private final ApprovalService approvalService;
    private final OrderService orderService;
    private final SampleService sampleService;
    private final ApprovalView view;

    public ApprovalController(ApprovalService approvalService, OrderService orderService,
                               SampleService sampleService, ApprovalView view) {
        this.approvalService = approvalService;
        this.orderService = orderService;
        this.sampleService = sampleService;
        this.view = view;
    }

    public void run() {
        List<Order> reserved = orderService.findByStatus(OrderStatus.RESERVED);
        view.displayReservedList(reserved);
        if (reserved.isEmpty()) return;

        try {
            int idx = view.selectOrderIndex(reserved.size());
            if (idx < 0 || idx >= reserved.size()) return;
            Order order = reserved.get(idx);

            String action = view.selectAction();
            if ("1".equals(action)) {
                approve(order);
            } else if ("2".equals(action)) {
                approvalService.reject(order);
                view.displayMessage("주문 거절: " + order.getOrderId() + " → [REJECTED]");
            } else {
                view.displayMessage("[오류] 올바른 번호를 입력하세요.");
            }
        } catch (Exception e) {
            view.displayMessage("[오류] " + e.getMessage());
        }
    }

    private void approve(Order order) {
        Optional<Sample> sampleOpt = sampleService.findAll().stream()
                .filter(s -> s.getId().equals(order.getSampleId()))
                .findFirst();
        if (sampleOpt.isEmpty()) {
            view.displayMessage("[오류] 시료 정보를 찾을 수 없습니다.");
            return;
        }
        Sample sample = sampleOpt.get();

        if (ProductionCalculator.isProductionNeeded(order.getQuantity(), sample.getStock())) {
            int shortage = ProductionCalculator.calcShortage(order.getQuantity(), sample.getStock());
            int actualQty = ProductionCalculator.calcActualQty(shortage, sample.getYield());
            double totalTime = ProductionCalculator.calcTotalTime(actualQty, sample.getAvgProductionTime());

            boolean confirmed = view.confirmProductionInfo(shortage, actualQty, totalTime);
            if (!confirmed) {
                approvalService.reject(order);
                view.displayMessage("주문 거절: " + order.getOrderId() + " → [REJECTED]");
                return;
            }
        }
        approvalService.approve(order, sample);
        view.displayMessage("주문 승인: " + order.getOrderId() + " → [" + order.getStatus() + "]");
    }
}
