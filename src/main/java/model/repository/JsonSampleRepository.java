package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.Sample;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JsonSampleRepository extends AbstractJsonRepository implements SampleRepository {

    public JsonSampleRepository(String filePath) {
        super(filePath, new TypeToken<List<Sample>>() {}.getType(), "samples.json");
    }

    @Override
    public void save(Sample sample) {
        Objects.requireNonNull(sample);
        List<Sample> list = loadAll();
        list.removeIf(s -> s.getId().equals(sample.getId()));
        list.add(sample);
        persistAll(list);
    }

    @Override
    public List<Sample> findAll() { return loadAll(); }

    @Override
    public Optional<Sample> findById(String id) {
        Objects.requireNonNull(id);
        return this.<Sample>loadAll().stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public boolean delete(String id) {
        Objects.requireNonNull(id);
        List<Sample> list = loadAll();
        boolean removed = list.removeIf(s -> s.getId().equals(id));
        if (removed) persistAll(list);
        return removed;
    }
}
