package model.service;

public class ProductionCalculator {

    public static final double CORRECTION_FACTOR = 0.9;

    private ProductionCalculator() {}

    public static int calcShortage(int orderQty, int stock) {
        return orderQty - stock;
    }

    public static int calcActualQty(int shortage, double yield) {
        return (int) Math.ceil(shortage / (yield * CORRECTION_FACTOR));
    }

    public static double calcTotalTime(int actualQty, double avgProductionTime) {
        return avgProductionTime * actualQty;
    }

    public static boolean isProductionNeeded(int orderQty, int stock) {
        return orderQty > stock;
    }
}
