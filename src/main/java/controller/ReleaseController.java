package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.service.OrderService;
import model.service.ReleaseService;
import model.service.SampleService;
import view.ReleaseView;

import java.util.List;
import java.util.Optional;

public class ReleaseController {

    private final ReleaseService releaseService;
    private final OrderService orderService;
    private final SampleService sampleService;
    private final ReleaseView view;

    public ReleaseController(ReleaseService releaseService, OrderService orderService,
                              SampleService sampleService, ReleaseView view) {
        this.releaseService = releaseService;
        this.orderService = orderService;
        this.sampleService = sampleService;
        this.view = view;
    }

    public void run() {
        List<Order> confirmed = orderService.findByStatus(OrderStatus.CONFIRMED);
        view.displayConfirmedList(confirmed);
        if (confirmed.isEmpty()) return;

        try {
            int idx = view.selectOrderIndex(confirmed.size());
            if (idx < 0 || idx >= confirmed.size()) return;
            Order order = confirmed.get(idx);

            Optional<Sample> sampleOpt = sampleService.findAll().stream()
                    .filter(s -> s.getId().equals(order.getSampleId()))
                    .findFirst();
            if (sampleOpt.isEmpty()) {
                view.displayMessage("[오류] 시료 정보를 찾을 수 없습니다.");
                return;
            }
            releaseService.release(order, sampleOpt.get());
            view.displayReleaseResult(order);
        } catch (Exception e) {
            view.displayMessage("[오류] " + e.getMessage());
        }
    }
}
