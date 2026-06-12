package model.service;

import model.entity.ProductionJob;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ProductionQueue {

    private ProductionJob current;
    private final Deque<ProductionJob> waiting = new ArrayDeque<>();

    public void enqueue(ProductionJob job) {
        if (current == null) {
            current = job;
        } else {
            waiting.addLast(job);
        }
    }

    public void completeCurrentJob() {
        current = waiting.pollFirst();
    }

    public ProductionJob getCurrentJob() {
        return current;
    }

    public List<ProductionJob> getWaitingJobs() {
        return List.copyOf(waiting);
    }

    public int size() {
        return (current == null ? 0 : 1) + waiting.size();
    }

    public List<ProductionJob> snapshot() {
        List<ProductionJob> list = new java.util.ArrayList<>();
        if (current != null) list.add(current);
        list.addAll(waiting);
        return list;
    }
}
