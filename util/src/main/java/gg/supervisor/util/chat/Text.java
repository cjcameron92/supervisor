package gg.supervisor.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;
import java.util.regex.Pattern;

public class Text {

    private static final Pattern PATTERN = Pattern.compile("([^\\\\]?)&([0-9a-fk-r])");

    private static final Map<String, String> COLOR_CODES = new HashMap<>() {{

        put("0", "<reset><color:black>");
        put("1", "<reset><color:dark_blue>");
        put("2", "<reset><color:dark_green>");
        put("3", "<reset><color:dark_aqua>");
        put("4", "<reset><color:dark_red>");
        put("5", "<reset><color:dark_purple>");
        put("6", "<reset><color:gold>");
        put("7", "<reset><color:gray>");
        put("8", "<reset><color:dark_gray>");
        put("9", "<reset><color:blue>");
        put("a", "<reset><color:green>");
        put("b", "<reset><color:aqua>");
        put("c", "<reset><color:red>");
        put("d", "<reset><color:light_purple>");
        put("e", "<reset><color:yellow>");
        put("f", "<reset><color:white>");

        put("k", "<obfuscated>");
        put("l", "<bold>");
        put("m", "<strikethrough>");
        put("n", "<underlined>");
        put("o", "<italic>");
        put("r", "<reset>");

    }};

    public static Component translate(String text) {

        return MiniMessage.miniMessage().deserialize(replacePrimitiveWithMiniMessage(text.replaceAll(String.valueOf(LegacyComponentSerializer.SECTION_CHAR), "&"))).decoration(TextDecoration.ITALIC, false);

    }

    public static Component[] translate(String... text) {
        return Arrays.stream(text).map(Text::translate).toArray(Component[]::new);
    }

    public static List<Component> translate(List<String> list) {

        final List<Component> toReturn = new ArrayList<>();

        for (String text : list) {
            toReturn.add(translate(text.replaceAll(String.valueOf(LegacyComponentSerializer.SECTION_CHAR), "&")));
        }

        return toReturn;

    }

    private static String replacePrimitiveWithMiniMessage(String string) {
        return PATTERN.matcher(string).replaceAll(matchResult -> matchResult.group(1) + COLOR_CODES.get(matchResult.group(2)));
    }


    public static String translateToPrimitive(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static List<String> translateToPrimitive(List<Component> component) {
        return component.stream().map(Text::translateToPrimitive).toList();
    }

    public static String translateToMiniMessage(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static List<String> translateToMiniMessage(List<Component> component) {
        return component.stream().map(Text::translateToMiniMessage).toList();
    }

    public static String capitalize(String s) {

        StringBuilder stringBuilder = new StringBuilder();

        String[] words = s.split(" +");

        for (String word : words) {
            stringBuilder.append(word.substring(0, 1).toUpperCase());
            stringBuilder.append(word.substring(1).toLowerCase());
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }

}