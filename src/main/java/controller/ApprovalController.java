package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.service.ApprovalService;
import model.service.OrderService;
import model.service.SampleService;
import view.ApprovalView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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
        Map<String, String> sampleNames = buildSampleNameMap();
        view.displayReservedList(reserved, sampleNames);
        if (reserved.isEmpty()) return;

        try {
            int idx = view.selectOrderIndex(reserved.size());
            if (idx < 0 || idx >= reserved.size()) return;
            Order order = reserved.get(idx);

            String action = view.selectAction();
            if ("1".equals(action)) {
                approve(order, sampleNames);
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

    private void approve(Order order, Map<String, String> sampleNames) {
        Optional<Sample> sampleOpt = sampleService.findById(order.getSampleId());
        if (sampleOpt.isEmpty()) {
            view.displayMessage("[오류] 시료 정보를 찾을 수 없습니다.");
            return;
        }
        Sample sample = sampleOpt.get();
        String sampleName = sampleNames.getOrDefault(sample.getId(), sample.getId());

        ApprovalService.ProductionPlan plan = approvalService.calcProductionPlan(order, sample).orElse(null);
        if (plan != null) {
            boolean confirmed = view.confirmProductionInfo(
                    sampleName, sample.getStock(), plan.shortage(), plan.actualQty(), plan.totalTime());
            if (!confirmed) {
                view.displayMessage("생산 승인을 취소하였습니다. 주문은 RESERVED 상태로 유지됩니다.");
                return;
            }
        }
        OrderStatus before = OrderStatus.RESERVED;
        approvalService.approve(order, sample);
        view.displayApprovalResult(order.getOrderId(), before, order.getStatus());
    }

    private Map<String, String> buildSampleNameMap() {
        return sampleService.findAll().stream()
                .collect(Collectors.toMap(Sample::getId, Sample::getName));
    }
}
