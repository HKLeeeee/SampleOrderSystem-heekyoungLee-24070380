package model.entity;

public class ProductionJob {

    private final String orderId;
    private final String sampleId;
    private final int shortage;
    private final int actualQty;
    private final double avgProductionTime;

    public ProductionJob(String orderId, String sampleId, int shortage, int actualQty, double avgProductionTime) {
        this.orderId = orderId;
        this.sampleId = sampleId;
        this.shortage = shortage;
        this.actualQty = actualQty;
        this.avgProductionTime = avgProductionTime;
    }

    public String getOrderId() { return orderId; }
    public String getSampleId() { return sampleId; }
    public int getShortage() { return shortage; }
    public int getActualQty() { return actualQty; }
    public double getAvgProductionTime() { return avgProductionTime; }
}
