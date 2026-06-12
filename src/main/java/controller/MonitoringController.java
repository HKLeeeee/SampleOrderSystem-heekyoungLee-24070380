package controller;

import model.entity.Order;
import model.entity.Sample;
import model.service.MonitoringService;
import model.service.OrderService;
import model.service.SampleService;
import view.MonitoringView;

import java.util.ArrayList;
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
        while (true) {
            String choice = view.readSubMenu();
            switch (choice) {
                case "1" -> showOrderCounts();
                case "2" -> showStockStatus();
                case "0" -> { return; }
                default -> view.displayMessage("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private void showOrderCounts() {
        List<Order> allOrders = orderService.findAll();
        view.displayOrderCounts(monitoringService.getOrderCountByStatus(allOrders));
    }

    private void showStockStatus() {
        List<Order> allOrders = orderService.findAll();
        List<Sample> samples = sampleService.findAll();
        List<MonitoringView.StockRow> rows = new ArrayList<>();
        for (Sample sample : samples) {
            List<Order> sampleOrders = allOrders.stream()
                    .filter(o -> o.getSampleId().equals(sample.getId()))
                    .toList();
            String status = monitoringService.getStockStatus(sample, sampleOrders);
            int ratio = monitoringService.getStockRatio(sample, sampleOrders);
            rows.add(new MonitoringView.StockRow(sample.getName(), sample.getStock(), status, ratio));
        }
        view.displayStockStatus(rows);
    }
}
