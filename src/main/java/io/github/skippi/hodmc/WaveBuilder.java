package io.github.skippi.hodmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class WaveBuilder {
    private List<String> units = new ArrayList<>();
    private long timeLimit = 1200;

    public WaveBuilder withUnitGroup(String unitId, int count) {
        units.addAll(Collections.nCopies(count, unitId));
        return this;
    }

    public Wave build() {
        return new Wave(units, timeLimit);
    }
}
