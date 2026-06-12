package controller;

public interface DashboardProvider {
    int getSampleCount();
    long getTotalStock();
    long getTotalOrderCount();
    int getProductionQueueSize();
}
