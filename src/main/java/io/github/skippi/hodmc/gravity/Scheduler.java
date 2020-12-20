package io.github.skippi.hodmc.gravity;

import java.util.ArrayDeque;
import java.util.Queue;

public class Scheduler {
    private Queue<Action> queue = new ArrayDeque<>();

    public void schedule(Action action) {
        queue.add(action);
    }

    public void tick() {
        double weight = 0.0;
        while (!queue.isEmpty() && weight < 1.0) {
            System.out.println(queue.size());
            Action action = queue.remove();
            action.call(this);
            weight += action.getWeight();
        }
    }
}
