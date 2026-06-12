import controller.*;
import model.repository.*;
import model.service.*;
import view.*;

import java.util.Map;
import java.util.Scanner;

public class AppContext implements DashboardProvider {

    private final SampleService sampleService;
    private final OrderService orderService;
    private final ApprovalService approvalService;
    private final ProductionService productionService;
    private final MonitoringService monitoringService;
    private final ReleaseService releaseService;
    private final ProductionQueue productionQueue;

    public AppContext(String dataDir) {
        SampleRepository sampleRepo = new JsonSampleRepository(dataDir + "/samples.json");
        OrderRepository orderRepo = new JsonOrderRepository(dataDir + "/orders.json");
        ProductionQueueRepository queueRepo = new JsonProductionQueueRepository(dataDir + "/queue.json");

        this.productionQueue = new ProductionQueue();
        queueRepo.load().forEach(productionQueue::enqueue);

        this.sampleService = new SampleService(sampleRepo);
        this.orderService = new OrderService(sampleRepo, orderRepo);
        this.approvalService = new ApprovalService(orderRepo, productionQueue);
        this.productionService = new ProductionService(productionQueue, sampleRepo, orderRepo);
        this.monitoringService = new MonitoringService();
        this.releaseService = new ReleaseService(sampleRepo, orderRepo);
    }

    public MainController buildMainController(Scanner scanner) {
        SampleView sampleView = new SampleView(scanner);
        OrderView orderView = new OrderView(scanner);
        ApprovalView approvalView = new ApprovalView(scanner);
        ProductionView productionView = new ProductionView(scanner);
        MonitoringView monitoringView = new MonitoringView();
        ReleaseView releaseView = new ReleaseView(scanner);
        MainMenuView mainMenuView = new MainMenuView();

        SampleController sampleCtrl = new SampleController(sampleService, sampleView);
        OrderController orderCtrl = new OrderController(orderService, sampleService, orderView);
        ApprovalController approvalCtrl = new ApprovalController(approvalService, orderService, sampleService, approvalView);
        ProductionController productionCtrl = new ProductionController(productionService, productionQueue, productionView);
        MonitoringController monitoringCtrl = new MonitoringController(monitoringService, orderService, sampleService, monitoringView);
        ReleaseController releaseCtrl = new ReleaseController(releaseService, orderService, sampleService, releaseView);

        Map<Integer, Runnable> handlers = Map.of(
                1, sampleCtrl::run,
                2, orderCtrl::run,
                3, approvalCtrl::run,
                4, monitoringCtrl::run,
                5, productionCtrl::run,
                6, releaseCtrl::run
        );

        return new MainController(mainMenuView, handlers, this, scanner);
    }

    @Override
    public int getSampleCount() { return sampleService.findAll().size(); }
    @Override
    public long getTotalStock() { return sampleService.findAll().stream().mapToLong(s -> s.getStock()).sum(); }
    @Override
    public long getTotalOrderCount() { return orderService.findAll().size(); }
    @Override
    public int getProductionQueueSize() { return productionQueue.size(); }

    public SampleService getSampleService() { return sampleService; }
    public OrderService getOrderService() { return orderService; }
    public ApprovalService getApprovalService() { return approvalService; }
    public ProductionService getProductionService() { return productionService; }
    public MonitoringService getMonitoringService() { return monitoringService; }
    public ReleaseService getReleaseService() { return releaseService; }
    public ProductionQueue getProductionQueue() { return productionQueue; }
}
