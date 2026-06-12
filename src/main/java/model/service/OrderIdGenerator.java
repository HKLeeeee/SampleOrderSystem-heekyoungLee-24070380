package model.service;

import java.util.HashMap;
import java.util.Map;

public class OrderIdGenerator {

    private final Map<String, Integer> sequenceByDate = new HashMap<>();

    public String generate(String date) {
        int seq = sequenceByDate.merge(date, 1, Integer::sum);
        return String.format("ORD-%s-%04d", date, seq);
    }
}
