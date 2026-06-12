package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.Sample;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class JsonSampleRepository implements SampleRepository {

    private static final Type LIST_TYPE = new TypeToken<List<Sample>>() {}.getType();

    private final Path filePath;

    public JsonSampleRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) Files.writeString(filePath, "[]", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("저장소 파일 초기화 실패: " + filePath, e);
        }
    }

    private List<Sample> loadAll() {
        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) return new ArrayList<>();
            List<Sample> list = GsonConfig.INSTANCE.fromJson(json, LIST_TYPE);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 실패: " + filePath, e);
        } catch (Exception e) {
            System.err.println("[경고] samples.json 파싱 오류 — 빈 목록으로 초기화. 원인: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void persistAll(List<Sample> samples) {
        try {
            Files.writeString(filePath, GsonConfig.INSTANCE.toJson(samples), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 쓰기 실패: " + filePath, e);
        }
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
        return loadAll().stream().filter(s -> s.getId().equals(id)).findFirst();
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
