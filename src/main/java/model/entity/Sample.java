package model.entity;

public class Sample {

    private final String id;
    private final String name;
    private final double avgProductionTime;
    private final double yield;
    private int stock;

    public Sample(String id, String name, double avgProductionTime, double yield, int stock) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("시료 ID는 필수입니다.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("시료 이름은 필수입니다.");
        if (avgProductionTime <= 0) throw new IllegalArgumentException("평균 생산시간은 0 초과여야 합니다.");
        if (yield <= 0 || yield > 1.0) throw new IllegalArgumentException("수율은 0 초과 1 이하여야 합니다.");
        if (stock < 0) throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");

        this.id = id;
        this.name = name;
        this.avgProductionTime = avgProductionTime;
        this.yield = yield;
        this.stock = stock;
    }

    public void addStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("추가 수량은 0 초과여야 합니다.");
        this.stock += amount;
    }

    public void deductStock(int amount) {
        if (amount > this.stock) throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stock);
        this.stock -= amount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getAvgProductionTime() { return avgProductionTime; }
    public double getYield() { return yield; }
    public int getStock() { return stock; }
}
