package model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    @DisplayName("RESERVED → CONFIRMED 허용")
    void reserved_to_confirmed() {
        assertDoesNotThrow(() -> OrderStatus.RESERVED.transitionTo(OrderStatus.CONFIRMED));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.RESERVED.transitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    @DisplayName("RESERVED → PRODUCING 허용")
    void reserved_to_producing() {
        assertDoesNotThrow(() -> OrderStatus.RESERVED.transitionTo(OrderStatus.PRODUCING));
        assertEquals(OrderStatus.PRODUCING, OrderStatus.RESERVED.transitionTo(OrderStatus.PRODUCING));
    }

    @Test
    @DisplayName("RESERVED → REJECTED 허용")
    void reserved_to_rejected() {
        assertDoesNotThrow(() -> OrderStatus.RESERVED.transitionTo(OrderStatus.REJECTED));
        assertEquals(OrderStatus.REJECTED, OrderStatus.RESERVED.transitionTo(OrderStatus.REJECTED));
    }

    @Test
    @DisplayName("PRODUCING → CONFIRMED 허용")
    void producing_to_confirmed() {
        assertDoesNotThrow(() -> OrderStatus.PRODUCING.transitionTo(OrderStatus.CONFIRMED));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.PRODUCING.transitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    @DisplayName("CONFIRMED → RELEASE 허용")
    void confirmed_to_release() {
        assertDoesNotThrow(() -> OrderStatus.CONFIRMED.transitionTo(OrderStatus.RELEASE));
        assertEquals(OrderStatus.RELEASE, OrderStatus.CONFIRMED.transitionTo(OrderStatus.RELEASE));
    }

    @Test
    @DisplayName("CONFIRMED → PRODUCING 금지")
    void confirmed_to_producing_금지() {
        assertThrows(IllegalStateException.class,
                () -> OrderStatus.CONFIRMED.transitionTo(OrderStatus.PRODUCING));
    }

    @Test
    @DisplayName("REJECTED → CONFIRMED 금지")
    void rejected_to_confirmed_금지() {
        assertThrows(IllegalStateException.class,
                () -> OrderStatus.REJECTED.transitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    @DisplayName("RELEASE → CONFIRMED 금지")
    void release_to_confirmed_금지() {
        assertThrows(IllegalStateException.class,
                () -> OrderStatus.RELEASE.transitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    @DisplayName("PRODUCING → RESERVED 금지")
    void producing_to_reserved_금지() {
        assertThrows(IllegalStateException.class,
                () -> OrderStatus.PRODUCING.transitionTo(OrderStatus.RESERVED));
    }
}
