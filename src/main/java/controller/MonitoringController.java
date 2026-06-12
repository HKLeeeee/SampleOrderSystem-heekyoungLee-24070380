package controller;

import model.entity.Order;
import model.entity.Sample;
import model.service.MonitoringService;
import model.service.OrderService;
import model.service.SampleService;
import view.MonitoringView;

import java.util.List;

public class MonitoringController {

    private final MonitoringService monitoringService;
    private final OrderService orderService;
    private final SampleService sampleService;
    private final MonitoringView view;

    public MonitoringController(MonitoringService monitoringService, OrderService orderService,
                                 SampleService sampleService, MonitoringView view) {
        this.monitoringService = monitoringService;
        this.orderService = orderService;
        this.sampleService = sampleService;
        this.view = view;
    }

    public void run() {
        List<Order> allOrders = getAllOrders();

        view.displayOrderCounts(monitoringService.getOrderCountByStatus(allOrders));

        List<Sample> samples = sampleService.findAll();
        view.displayStockStatus(samples, sample -> {
            List<Order> sampleOrders = allOrders.stream()
                    .filter(o -> o.getSampleId().equals(sample.getId()))
                    .toList();
            return monitoringService.getStockStatus(sample, sampleOrders);
        });
    }

    private List<Order> getAllOrders() {
        return orderService.findAll();
    }
}
