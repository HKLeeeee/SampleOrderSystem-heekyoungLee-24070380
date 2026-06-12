package model.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductionCalculatorTest {

    @Test
    @DisplayName("부족분 계산 — 주문 200, 재고 30 → 170")
    void 부족분_계산() {
        assertEquals(170, ProductionCalculator.calcShortage(200, 30));
    }

    @Test
    @DisplayName("실생산량 PRD 검증 예시 — 부족 170, 수율 0.92 → 206")
    void 실생산량_PRD_검증예시() {
        assertEquals(206, ProductionCalculator.calcActualQty(170, 0.92));
    }

    @Test
    @DisplayName("실생산량 소수점 올림 — 부족 50, 수율 0.92 → 61")
    void 실생산량_소수점_올림() {
        assertEquals(61, ProductionCalculator.calcActualQty(50, 0.92));
    }

    @Test
    @DisplayName("총 생산시간 계산 — 실생산량 206, avgTime 0.8 → 164.8")
    void 총생산시간_계산() {
        assertEquals(164.8, ProductionCalculator.calcTotalTime(206, 0.8), 0.001);
    }

    @Test
    @DisplayName("재고 충분 — 주문 50, 재고 100 → 생산 불필요")
    void 재고_충분_생산_불필요() {
        assertFalse(ProductionCalculator.isProductionNeeded(50, 100));
    }

    @Test
    @DisplayName("부족분 정확히 0 — 주문 100, 재고 100 → 생산 불필요")
    void 부족분_정확히_0() {
        assertFalse(ProductionCalculator.isProductionNeeded(100, 100));
    }

    @Test
    @DisplayName("CORRECTION_FACTOR 상수 공개 접근 — 매직넘버 없이 기대값 계산")
    void CORRECTION_FACTOR_공개_접근_가능() {
        double factor = ProductionCalculator.CORRECTION_FACTOR;
        assertEquals(0.9, factor, 0.0001);
        // 상수로 기대값 계산 (하드코딩 제거)
        int expected = (int) Math.ceil(170.0 / (0.92 * factor));
        assertEquals(expected, ProductionCalculator.calcActualQty(170, 0.92));
    }

    @Test
    @DisplayName("실생산량 — 수율 1.0 경계값")
    void 실생산량_수율_1_경계값() {
        // ceil(100 / (1.0 * 0.9)) = ceil(111.11) = 112
        int expected = (int) Math.ceil(100.0 / (1.0 * ProductionCalculator.CORRECTION_FACTOR));
        assertEquals(expected, ProductionCalculator.calcActualQty(100, 1.0));
    }

    @Test
    @DisplayName("재고 초과 — 주문 50, 재고 200 → 부족분 음수이므로 생산 불필요")
    void 재고_초과_생산_불필요() {
        assertFalse(ProductionCalculator.isProductionNeeded(50, 200));
        assertEquals(-150, ProductionCalculator.calcShortage(50, 200));
    }
}
