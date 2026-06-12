package model.entity;

public class Order {

    private final String orderId;
    private final String sampleId;
    private final String customerName;
    private final int quantity;
    private OrderStatus status;

    public Order(String orderId, String sampleId, String customerName, int quantity) {
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("주문번호는 필수입니다.");
        if (sampleId == null || sampleId.isBlank()) throw new IllegalArgumentException("시료 ID는 필수입니다.");
        if (customerName == null || customerName.isBlank()) throw new IllegalArgumentException("고객명은 필수입니다.");
        if (quantity <= 0) throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");

        this.orderId = orderId;
        this.sampleId = sampleId;
        this.customerName = customerName;
        this.quantity = quantity;
        this.status = OrderStatus.RESERVED;
    }

    public void changeStatus(OrderStatus next) {
        this.status = this.status.transitionTo(next);
    }

    public String getOrderId() { return orderId; }
    public String getSampleId() { return sampleId; }
    public String getCustomerName() { return customerName; }
    public int getQuantity() { return quantity; }
    public OrderStatus getStatus() { return status; }
}
