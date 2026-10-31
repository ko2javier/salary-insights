package com.salary.salary_insights.seed;

import java.util.SequencedMap;

/**
 * Picks an option with a probability proportional to its weight.
 * Takes a SequencedMap because the order of the options must be the same on every run:
 * Map.of changes its iteration order between JVM runs, and the same seed would then
 * produce different employees.
 */
final class WeightedChoice<T> {

    private final SequencedMap<T, Integer> weights;
    private final int totalWeight;

    WeightedChoice(SequencedMap<T, Integer> weights) {
        this.weights = weights;
        this.totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** @param roll a number in [0, 1), such as Random.nextDouble() */
    T pick(double roll) {
        double target = roll * totalWeight;
        int cumulative = 0;
        for (var option : weights.entrySet()) {
            cumulative += option.getValue();
            if (target < cumulative) {
                return option.getKey();
            }
        }
        // Only reachable through floating-point rounding, with a roll just below 1
        return weights.lastEntry().getKey();
    }
}
