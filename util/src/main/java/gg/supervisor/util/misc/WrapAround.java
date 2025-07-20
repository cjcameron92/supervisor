package gg.supervisor.util.misc;

import java.util.ArrayList;
import java.util.List;

public class WrapAround {

    public static List<String> of(String input, int charLimit) {
        if (input == null || input.isEmpty() || charLimit <= 0) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        String[] words = input.split(" "); // Split the input by spaces
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {

            if (currentLine.length() + word.length() > charLimit) {
                result.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }

            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString().trim());
        }

        return result;
    }

    public static List<String> of(List<String> input, int charLimit) {

        final List<String> toReturn = new ArrayList<>();

        if (input == null || input.isEmpty() || charLimit <= 0) {
            return new ArrayList<>();
        }

        for (String s : input) {
            toReturn.addAll(of(s, charLimit));
        }

        return toReturn;
    }

}
