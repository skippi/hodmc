package io.github.skippi.hodmc.gravity;

public interface Action {
    default double getWeight() { return 1.0; }
    void call(Scheduler scheduler);
}
