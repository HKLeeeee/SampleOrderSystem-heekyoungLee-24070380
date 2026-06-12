package model.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractJsonRepository {

    protected final Path filePath;
    private final Type listType;
    private final String fileName;

    protected AbstractJsonRepository(String filePath, Type listType, String fileName) {
        this.filePath = Paths.get(filePath);
        this.listType = listType;
        this.fileName = fileName;
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (this.filePath.getParent() != null) Files.createDirectories(this.filePath.getParent());
            if (!Files.exists(this.filePath)) Files.writeString(this.filePath, "[]", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("저장소 파일 초기화 실패: " + this.filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> loadAll() {
        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) return new ArrayList<>();
            List<T> list = GsonConfig.INSTANCE.fromJson(json, listType);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 실패: " + filePath, e);
        } catch (Exception e) {
            System.err.println("[경고] " + fileName + " 파싱 오류 — 빈 목록으로 초기화. 원인: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    protected <T> void persistAll(List<T> items) {
        try {
            Files.writeString(filePath, GsonConfig.INSTANCE.toJson(items), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 쓰기 실패: " + filePath, e);
        }
    }
}
