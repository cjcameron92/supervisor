package gg.supervisor.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.regex.Pattern;

public class Text {

    private static final Pattern PATTERN = Pattern.compile("([^\\\\]?)&([0-9a-fk-r])");

    private static final Map<String, String> COLOR_CODES = new HashMap<>() {{

        put("0", "<color:black>");
        put("1", "<color:dark_blue>");
        put("2", "<color:dark_green>");
        put("3", "<color:dark_aqua>");
        put("4", "<color:dark_red>");
        put("5", "<color:dark_purple>");
        put("6", "<color:gold>");
        put("7", "<color:gray>");
        put("8", "<color:dark_gray>");
        put("9", "<color:blue>");
        put("a", "<color:green>");
        put("b", "<color:aqua>");
        put("c", "<color:red>");
        put("d", "<color:light_purple>");
        put("e", "<color:yellow>");
        put("f", "<color:white>");

        put("k", "<obfuscated>");
        put("l", "<bold>");
        put("m", "<strikethrough>");
        put("n", "<underlined>");
        put("o", "<italic>");
        put("r", "<reset>");

    }};

    public static Component translate(String text) {
        return MiniMessage.miniMessage().deserialize(replacePrimitiveWithMiniMessage(text)).decoration(TextDecoration.ITALIC, false);
    }

    public static Component[] translate(String... text) {

        return Arrays.stream(text).map(Text::translate).toArray(Component[]::new);

    }
    
    public static List<Component> translate(List<String> list) {

        final List<Component> toReturn = new ArrayList<>();

        for (String text : list) {
            toReturn.add(translate(text));
        }

        return toReturn;

    }

    private static String replacePrimitiveWithMiniMessage(String string) {
        return PATTERN.matcher(string).replaceAll(matchResult -> matchResult.group(1) + COLOR_CODES.get(matchResult.group(2)));
    }
}
