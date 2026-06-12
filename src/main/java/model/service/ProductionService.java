package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.ProductionJob;
import model.entity.Sample;
import model.repository.OrderRepository;
import model.repository.ProductionQueueRepository;
import model.repository.SampleRepository;

import java.time.LocalDateTime;

public class ProductionService {

    private final ProductionQueue queue;
    private final SampleRepository sampleRepository;
    private final OrderRepository orderRepository;
    private final ProductionQueueRepository queueRepository;

    public ProductionService(ProductionQueue queue, SampleRepository sampleRepository,
                             OrderRepository orderRepository, ProductionQueueRepository queueRepository) {
        this.queue = queue;
        this.sampleRepository = sampleRepository;
        this.orderRepository = orderRepository;
        this.queueRepository = queueRepository;
    }

    public void completeCurrentProduction() {
        ProductionJob job = queue.getCurrentJob();
        if (job == null) return;

        Sample sample = sampleRepository.findById(job.getSampleId())
                .orElseThrow(() -> new IllegalStateException("시료를 찾을 수 없습니다: " + job.getSampleId()));
        Order order = orderRepository.findById(job.getOrderId())
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다: " + job.getOrderId()));

        sample.addStock(job.getActualQty());
        sampleRepository.save(sample);

        order.changeStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        queue.completeCurrentJob();
        queueRepository.save(queue.snapshot());
    }

    public LocalDateTime getExpectedEndTime(ProductionJob job, LocalDateTime startTime) {
        double totalMinutes = ProductionCalculator.calcTotalTime(job.getActualQty(), job.getAvgProductionTime());
        long seconds = (long) (totalMinutes * 60);
        return startTime.plusSeconds(seconds);
    }
}
