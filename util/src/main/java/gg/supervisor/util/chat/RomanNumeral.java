package gg.supervisor.util.chat;

import java.util.TreeMap;

public class RomanNumeral {

    private static final TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static String toRoman(int number) {
        StringBuilder result = new StringBuilder();

        for (Integer key : map.descendingKeySet()) {
            while (number >= key) {
                result.append(map.get(key));
                number -= key;
            }
        }

        return result.toString();
    }
}
