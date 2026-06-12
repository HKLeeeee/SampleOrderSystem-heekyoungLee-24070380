package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.ProductionJob;
import model.entity.Sample;
import model.repository.OrderRepository;
import model.repository.ProductionQueueRepository;

import java.util.Optional;

public class ApprovalService {

    public static final class ProductionPlan {
        private final int shortage;
        private final int actualQty;
        private final double totalTime;

        public ProductionPlan(int shortage, int actualQty, double totalTime) {
            this.shortage = shortage;
            this.actualQty = actualQty;
            this.totalTime = totalTime;
        }

        public int shortage() { return shortage; }
        public int actualQty() { return actualQty; }
        public double totalTime() { return totalTime; }
    }

    private final OrderRepository orderRepository;
    private final ProductionQueue productionQueue;
    private final ProductionQueueRepository queueRepository;

    public ApprovalService(OrderRepository orderRepository, ProductionQueue productionQueue,
                           ProductionQueueRepository queueRepository) {
        this.orderRepository = orderRepository;
        this.productionQueue = productionQueue;
        this.queueRepository = queueRepository;
    }

    public Optional<ProductionPlan> calcProductionPlan(Order order, Sample sample) {
        if (!ProductionCalculator.isProductionNeeded(order.getQuantity(), sample.getStock())) {
            return Optional.empty();
        }
        int shortage = ProductionCalculator.calcShortage(order.getQuantity(), sample.getStock());
        int actualQty = ProductionCalculator.calcActualQty(shortage, sample.getYield());
        double totalTime = ProductionCalculator.calcTotalTime(actualQty, sample.getAvgProductionTime());
        return Optional.of(new ProductionPlan(shortage, actualQty, totalTime));
    }

    public void approve(Order order, Sample sample) {
        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new IllegalStateException("RESERVED 상태 주문만 승인할 수 있습니다. 현재 상태: " + order.getStatus());
        }
        if (ProductionCalculator.isProductionNeeded(order.getQuantity(), sample.getStock())) {
            int shortage = ProductionCalculator.calcShortage(order.getQuantity(), sample.getStock());
            int actualQty = ProductionCalculator.calcActualQty(shortage, sample.getYield());
            ProductionJob job = new ProductionJob(order.getOrderId(), sample.getId(), shortage, actualQty, sample.getAvgProductionTime());
            order.changeStatus(OrderStatus.PRODUCING);
            productionQueue.enqueue(job);
            queueRepository.save(productionQueue.snapshot());
        } else {
            order.changeStatus(OrderStatus.CONFIRMED);
        }
        orderRepository.save(order);
    }

    public void reject(Order order) {
        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new IllegalStateException("RESERVED 상태 주문만 거절할 수 있습니다. 현재 상태: " + order.getStatus());
        }
        order.changeStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }
}
