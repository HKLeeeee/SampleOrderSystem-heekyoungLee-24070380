package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.ProductionJob;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class JsonProductionQueueRepository implements ProductionQueueRepository {

    private static final Type LIST_TYPE = new TypeToken<List<ProductionJob>>() {}.getType();

    private final Path filePath;

    public JsonProductionQueueRepository(String filePath) {
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

    @Override
    public void save(List<ProductionJob> jobs) {
        Objects.requireNonNull(jobs);
        try {
            Files.writeString(filePath, GsonConfig.INSTANCE.toJson(jobs), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("큐 파일 쓰기 실패: " + filePath, e);
        }
    }

    @Override
    public List<ProductionJob> load() {
        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) return new ArrayList<>();
            List<ProductionJob> list = GsonConfig.INSTANCE.fromJson(json, LIST_TYPE);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw new UncheckedIOException("큐 파일 읽기 실패: " + filePath, e);
        } catch (Exception e) {
            System.err.println("[경고] queue.json 파싱 오류 — 빈 큐로 초기화. 원인: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
