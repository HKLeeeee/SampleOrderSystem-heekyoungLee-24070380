package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.ProductionJob;

import java.util.List;
import java.util.Objects;

public class JsonProductionQueueRepository extends AbstractJsonRepository implements ProductionQueueRepository {

    public JsonProductionQueueRepository(String filePath) {
        super(filePath, new TypeToken<List<ProductionJob>>() {}.getType(), "queue.json");
    }

    @Override
    public void save(List<ProductionJob> jobs) {
        Objects.requireNonNull(jobs);
        persistAll(jobs);
    }

    @Override
    public List<ProductionJob> load() {
        return loadAll();
    }
}
