package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.repository.OrderRepository;
import model.repository.SampleRepository;

public class ReleaseService {

    private final SampleRepository sampleRepository;
    private final OrderRepository orderRepository;

    public ReleaseService(SampleRepository sampleRepository, OrderRepository orderRepository) {
        this.sampleRepository = sampleRepository;
        this.orderRepository = orderRepository;
    }

    public void release(Order order, Sample sample) {
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("CONFIRMED 상태 주문만 출고할 수 있습니다. 현재 상태: " + order.getStatus());
        }
        if (sample.getStock() < order.getQuantity()) {
            throw new IllegalStateException("재고 부족으로 출고 불가. 재고: " + sample.getStock() + ", 주문량: " + order.getQuantity());
        }
        sample.deductStock(order.getQuantity());
        sampleRepository.save(sample);

        order.changeStatus(OrderStatus.RELEASE);
        orderRepository.save(order);
    }
}
