package model.repository;

import model.entity.ProductionJob;

import java.util.List;

public interface ProductionQueueRepository {
    void save(List<ProductionJob> jobs);
    List<ProductionJob> load();
}
