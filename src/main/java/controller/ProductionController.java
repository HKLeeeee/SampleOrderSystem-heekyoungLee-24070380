package controller;

import model.entity.ProductionJob;
import model.service.ProductionQueue;
import model.service.ProductionService;
import view.ProductionView;

import java.time.LocalDateTime;

public class ProductionController {

    private final ProductionService productionService;
    private final ProductionQueue queue;
    private final ProductionView view;

    public ProductionController(ProductionService productionService, ProductionQueue queue, ProductionView view) {
        this.productionService = productionService;
        this.queue = queue;
        this.view = view;
    }

    public void run() {
        ProductionJob current = queue.getCurrentJob();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedEnd = current != null
                ? productionService.getExpectedEndTime(current, now)
                : null;

        view.displayHeader();
        view.displayCurrentJob(current, now, expectedEnd);
        view.displayWaitingQueue(queue.getWaitingJobs(),
                expectedEnd != null ? expectedEnd : now);

        if (current != null && view.askCompleteProduction()) {
            productionService.completeCurrentProduction();
            view.displayMessage("생산 완료 처리됨. 주문이 CONFIRMED 상태로 전환되었습니다.");
        }
    }
}
