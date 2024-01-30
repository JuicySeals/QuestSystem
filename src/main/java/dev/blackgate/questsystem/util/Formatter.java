package dev.blackgate.questsystem.util;

import org.apache.commons.text.WordUtils;

public class Formatter {
    public static String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}
