package gg.supervisor.util.selector;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * A weighted random selector that allows selecting elements based on their weights.
 * This class provides methods to select a single element or multiple elements following the weights specified.
 * The elements must implement the Weighted interface to provide their respective weights.
 *
 * @param <T> the type of elements to select
 */
public class WeightedRandomSelector<T extends Weighted> {
    /**
     * Represents a list of elements used in the WeightedRandomSelector.
     */
    private final List<T> elements;

    /**
     * Constructor for WeightedRandomSelector class.
     *
     * @param elements Array of elements to be used for weighted random selection
     */
    public WeightedRandomSelector(T[] elements) {
        this.elements = Arrays.asList(elements);
    }

    /**
     * Constructs a WeightedRandomSelector with the specified elements.
     *
     * @param elements a list of elements to select from
     */
    public WeightedRandomSelector(List<T> elements) {
        this.elements = elements;
    }

    /**
     * Selects a random element from the provided weight map based on their respective weights.
     *
     * @param weightMap a map containing elements of type T as keys and their corresponding weights as values
     * @return the randomly selected element based on the weights provided in the map
     */
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

    /**
     * Calculates the total weight based on the elements and function provided.
     *
     * @param weights   Collection of elements for which weights need to be calculated
     * @param function  Function to apply on each element to get its weight as a Double value
     * @return the total weight calculated from applying the function on all elements
     */
    private double calculateTotalWeight(@NotNull Iterable<T> weights, Function<T, Double> function) {
        double total = 0;
        for (T weight : weights) {
            total += function.apply(weight);
        }
        return total;
    }

    /**
     * Selects a single element from the elements list with the default behavior of disallowing repeating selections.
     *
     * @return the selected element
     */
    public T select() {
        return select(true);
    }

    /**
     * Selects a single element from the collection with a possibility of repeating elements.
     *
     * @param repeating a boolean indicating whether repeating elements are allowed in the selection
     * @return the selected element based on the specified criteria
     */
    public T select(boolean repeating) {
        return select(1, repeating, Weighted::getWeight).get(0);
    }

    /**
     * Selects a specified number of elements from the elements list based on their weight.
     *
     * @param amount The number of elements to select.
     * @param repeating Flag indicating whether elements can be selected multiple times.
     * @param weightFunction The function to calculate the weight of each element.
     * @return A list of selected elements.
     * @throws IllegalStateException if elements list is empty or if requesting more elements than available when repeating is off.
     */
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
