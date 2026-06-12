package model.service;

import model.entity.ProductionJob;

import java.util.ArrayDeque;
import java.util.Collections;
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
        return Collections.unmodifiableList(List.copyOf(waiting));
    }

    public int size() {
        return (current == null ? 0 : 1) + waiting.size();
    }
}
