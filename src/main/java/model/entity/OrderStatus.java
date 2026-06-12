package model.entity;

import java.util.Set;
import java.util.Map;

public enum OrderStatus {
    RESERVED, PRODUCING, CONFIRMED, REJECTED, RELEASE;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            RESERVED,  Set.of(CONFIRMED, PRODUCING, REJECTED),
            PRODUCING, Set.of(CONFIRMED),
            CONFIRMED, Set.of(RELEASE),
            REJECTED,  Set.of(),
            RELEASE,   Set.of()
    );

    public OrderStatus transitionTo(OrderStatus next) {
        if (!ALLOWED.get(this).contains(next)) {
            throw new IllegalStateException(this + " → " + next + " 전이는 허용되지 않습니다.");
        }
        return next;
    }
}
