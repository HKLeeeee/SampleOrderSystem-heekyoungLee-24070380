package model.service;

import model.entity.Sample;
import model.repository.SampleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SampleService {

    private final SampleRepository repository;

    public SampleService(SampleRepository repository) {
        this.repository = repository;
    }

    public void register(Sample sample) {
        if (repository.findById(sample.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 시료 ID입니다: " + sample.getId());
        }
        repository.save(sample);
    }

    public List<Sample> findAll() {
        return repository.findAll();
    }

    public Optional<Sample> findById(String id) {
        return repository.findById(id);
    }

    public List<Sample> search(String keyword) {
        String lower = keyword.toLowerCase();
        return repository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(lower)
                        || s.getId().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
