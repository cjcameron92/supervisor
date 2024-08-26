package gg.supervisor.common.chat;

import java.util.HashMap;
import java.util.Map;

public class SmallCaps {

    private static final Map<Character, Character> charMap = new HashMap<>();

    static {
        charMap.put('q', 'ǫ');
        charMap.put('w', 'ᴡ');
        charMap.put('e', 'ᴇ');
        charMap.put('r', 'ʀ');
        charMap.put('t', 'ᴛ');
        charMap.put('y', 'ʏ');
        charMap.put('u', 'ᴜ');
        charMap.put('i', 'ɪ');
        charMap.put('o', 'ᴏ');
        charMap.put('p', 'ᴘ');
        charMap.put('a', 'ᴀ');
        charMap.put('s', 'ꜱ');
        charMap.put('d', 'ᴅ');
        charMap.put('f', 'ꜰ');
        charMap.put('g', 'ɢ');
        charMap.put('h', 'ʜ');
        charMap.put('j', 'ᴊ');
        charMap.put('k', 'ᴋ');
        charMap.put('l', 'ʟ');
        charMap.put('z', 'ᴢ');
        charMap.put('c', 'ᴄ');
        charMap.put('v', 'ᴠ');
        charMap.put('b', 'ʙ');
        charMap.put('n', 'ɴ');
        charMap.put('m', 'ᴍ');
    }

    public static String translate(String input) {
        StringBuilder translated = new StringBuilder();

        for (char c : input.toLowerCase().toCharArray()) {
            translated.append(charMap.getOrDefault(c, c));
        }

        return translated.toString();
    }

}