package io.github.skippi.hodmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wave {
    private List<String> units;
    private long timeLimit;

    private Wave() {}

    public static WaveBuilder builder() {
        return new WaveBuilder();
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public List<String> getUnits() {
        return units;
    }

    public static class WaveBuilder {
        private final List<String> units = new ArrayList<>();

        private WaveBuilder() {}

        public WaveBuilder withUnitGroup(String unitId, int count) {
            units.addAll(Collections.nCopies(count, unitId));
            return this;
        }

        public Wave build() {
            Wave wave = new Wave();
            wave.units = units;
            wave.timeLimit = 1200;
            return wave;
        }
    }
}
