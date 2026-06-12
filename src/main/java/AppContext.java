import model.repository.*;
import model.service.*;

public class AppContext {

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

    public SampleService getSampleService() { return sampleService; }
    public OrderService getOrderService() { return orderService; }
    public ApprovalService getApprovalService() { return approvalService; }
    public ProductionService getProductionService() { return productionService; }
    public MonitoringService getMonitoringService() { return monitoringService; }
    public ReleaseService getReleaseService() { return releaseService; }
    public ProductionQueue getProductionQueue() { return productionQueue; }
}
