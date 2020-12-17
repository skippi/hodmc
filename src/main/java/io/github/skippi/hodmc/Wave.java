package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.EntityLiving;

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
}
