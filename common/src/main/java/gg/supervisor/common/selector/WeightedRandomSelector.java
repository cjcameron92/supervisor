package gg.supervisor.common.selector;

import java.util.*;
import java.util.function.Function;

public class WeightedRandomSelector<T extends Weighted> {
    private final List<T> elements;

    public WeightedRandomSelector(T[] elements) {
        this.elements = Arrays.asList(elements);
    }

    public WeightedRandomSelector(List<T> elements) {
        this.elements = elements;
    }

    public static <T> T selectRandomWeighted(Map<T, Double> weightMap) {
        double totalWeight = 0.0;
        for (Double weight : weightMap.values()) {
            totalWeight += weight;
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalWeight;

        for (Map.Entry<T, Double> entry : weightMap.entrySet()) {
            randomValue -= entry.getValue();
            if (randomValue <= 0.0) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("The weight map may be empty or contain non-positive weights.");
    }

    private double calculateTotalWeight(Iterable<T> weights, Function<T, Double> consumer) {
        double total = 0;
        for (T weight : weights) {
            total += consumer.apply(weight);
        }
        return total;
    }

    public T select() {
        return select(true);
    }

    public T select(boolean repeating) {
        return select(1, repeating, Weighted::getWeight).get(0);
    }

    public List<T> select(int amount, boolean repeating, Function<T, Double> weightFunction) {
        if (elements.isEmpty()) {
            throw new IllegalStateException("No elements to select from.");
        }

        if (elements.size() < amount && !repeating) {
            throw new IllegalStateException("Requesting more elements than there are while repeatingRewards is toggled off.");
        }

        final List<T> toReturn = new ArrayList<>();
        final List<T> tempClone = new ArrayList<>(elements);

        for (int i = 0; i < amount; i++) {

            double randomValue = new Random().nextDouble() * calculateTotalWeight(tempClone, weightFunction);

            T selected = tempClone.get(tempClone.size() - 1);

            for (T element : tempClone) {
                double weight = weightFunction.apply(element);

                if (randomValue < weight) {
                    selected = element;
                    break;
                }
                randomValue -= weight;
            }

            if (!repeating)
                tempClone.remove(selected);

            toReturn.add(selected);
        }

        return toReturn;
    }

}
