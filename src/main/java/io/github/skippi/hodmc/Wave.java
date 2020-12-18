package io.github.skippi.hodmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wave {
    private List<String> units;
    private long timeLimit;

    public static WaveBuilder builder() {
        return new WaveBuilder();
    }

    public Wave(List<String> units, long timeLimit) {
        this.timeLimit = timeLimit;
        this.units = units;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public List<String> getUnits() {
        return units;
    }

    public static class WaveBuilder {
        private List<String> units = new ArrayList<>();
        private long timeLimit = 1200;

        private WaveBuilder() {}

        public  WaveBuilder withUnitGroup(String unitId, int count) {
            units.addAll(Collections.nCopies(count, unitId));
            return this;
        }

        public Wave build() {
            return new Wave(units, timeLimit);
        }
    }
}
