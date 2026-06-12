package dummy;

import com.google.gson.GsonBuilder;
import model.entity.Sample;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DummyDataGenerator {

    private static final String DEFAULT_OUTPUT_PATH = "data/samples.json";
    private static final long DEFAULT_SEED = 42L;
    private static final int DEFAULT_COUNT = 10;

    private static final String[] SAMPLE_NAMES = {
        "실리콘 웨이퍼-8인치",
        "GaN 에피택셜-4인치",
        "SiC 파워기판-6인치",
        "포토레지스트-PR7",
        "산화막 웨이퍼-SiO2",
        "실리콘 웨이퍼-12인치",
        "InP 기판-3인치",
        "GaAs 에피택셜-6인치",
        "질화규소막-Si3N4",
        "SOI 웨이퍼-8인치",
        "사파이어 기판-4인치",
        "Ge 기판-6인치"
    };

    private static final double MIN_AVG_TIME = 0.2;
    private static final double MAX_AVG_TIME = 1.0;
    private static final double MIN_YIELD = 0.70;
    private static final double MAX_YIELD = 0.99;
    private static final int MIN_STOCK = 10;
    private static final int MAX_STOCK = 500;
    private static final int STOCK_DEPLETION_DENOM = 10;

    private final String outputPath;

    public DummyDataGenerator(String outputPath) {
        this.outputPath = outputPath;
    }

    public DummyDataGenerator() {
        this(DEFAULT_OUTPUT_PATH);
    }

    public List<Sample> generateSamples(int count) {
        return generateSamples(count, DEFAULT_SEED);
    }

    public List<Sample> generateSamples(int count, long seed) {
        Random random = new Random(seed);
        List<Sample> samples = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String id = String.format("S-%03d", i + 1);
            String name = SAMPLE_NAMES[i % SAMPLE_NAMES.length];
            double avgTime = round2(MIN_AVG_TIME + random.nextDouble() * (MAX_AVG_TIME - MIN_AVG_TIME));
            double yield = round2(MIN_YIELD + random.nextDouble() * (MAX_YIELD - MIN_YIELD));
            int stock = random.nextInt(STOCK_DEPLETION_DENOM) == 0
                    ? 0
                    : MIN_STOCK + random.nextInt(MAX_STOCK - MIN_STOCK + 1);
            samples.add(new Sample(id, name, avgTime, yield, stock));
        }
        return samples;
    }

    public void generate() {
        generate(DEFAULT_COUNT, DEFAULT_SEED);
    }

    public void generate(int count, long seed) {
        List<Sample> samples = generateSamples(count, seed);
        save(samples);
        printSummary(samples);
    }

    private void save(List<Sample> samples) {
        Path path = Paths.get(outputPath);
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(samples);
            Files.writeString(path, json, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("더미 데이터 저장 실패: " + path, e);
        }
    }

    private void printSummary(List<Sample> samples) {
        System.out.println("================================================================");
        System.out.println("  Dummy 데이터 생성 완료");
        System.out.println("================================================================");
        System.out.printf("  저장 경로: %s (%d건)%n", outputPath, samples.size());
        System.out.println();
        System.out.printf("  %-8s %-24s %14s  %6s  %8s%n", "ID", "시료명", "평균 생산시간", "수율", "현재 재고");
        System.out.println("  " + "-".repeat(68));
        for (Sample s : samples) {
            System.out.printf("  %-8s %-24s %10s  %5.0f%%  %6dea%n",
                    s.getId(), s.getName(),
                    formatTime(s.getAvgProductionTime()),
                    s.getYield() * 100,
                    s.getStock());
        }
        System.out.println("================================================================");
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    static String formatTime(double avgTime) {
        if (avgTime == Math.floor(avgTime)) return (int) avgTime + " min/ea";
        String s = String.valueOf(avgTime);
        return s.replaceAll("0+$", "").replaceAll("\\.$", "") + " min/ea";
    }

    public static void main(String[] args) {
        DummyDataGenerator gen = new DummyDataGenerator();
        gen.generate();
    }
}
