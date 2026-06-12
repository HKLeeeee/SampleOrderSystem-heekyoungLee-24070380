package model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Order 정상 생성 — getter 일치, 초기 상태 RESERVED")
    void order_정상_생성() {
        Order order = new Order("ORD-20260612-0001", "S-001", "홍길동", 100);

        assertEquals("ORD-20260612-0001", order.getOrderId());
        assertEquals("S-001", order.getSampleId());
        assertEquals("홍길동", order.getCustomerName());
        assertEquals(100, order.getQuantity());
        assertEquals(OrderStatus.RESERVED, order.getStatus());
    }

    @Test
    @DisplayName("고객명 빈값 거부")
    void order_고객명_빈값_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-20260612-0001", "S-001", "", 100));
    }

    @Test
    @DisplayName("수량 0 거부")
    void order_수량_0_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-20260612-0001", "S-001", "홍길동", 0));
    }

    @Test
    @DisplayName("수량 음수 거부")
    void order_수량_음수_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-20260612-0001", "S-001", "홍길동", -1));
    }

    @Test
    @DisplayName("상태 전이 위임 — changeStatus 후 상태 변경")
    void order_상태전이_위임() {
        Order order = new Order("ORD-20260612-0001", "S-001", "홍길동", 100);
        order.changeStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }
}
