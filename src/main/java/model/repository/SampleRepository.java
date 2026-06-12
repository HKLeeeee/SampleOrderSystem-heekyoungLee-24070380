package model.repository;

import model.entity.Sample;

import java.util.List;
import java.util.Optional;

public interface SampleRepository {
    void save(Sample sample);
    List<Sample> findAll();
    Optional<Sample> findById(String id);
    boolean delete(String id);
}
