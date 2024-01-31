package dev.blackgate.questsystem.util;

import org.apache.commons.text.WordUtils;

public class Formatter {

    private Formatter() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}
