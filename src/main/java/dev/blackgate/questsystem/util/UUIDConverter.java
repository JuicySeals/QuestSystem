package dev.blackgate.questsystem.util;

import java.util.UUID;

public class UUIDConverter {
    // More performance focused way to store UUID's (Stores it in half the size of VARCHAR)
    public static byte[] toByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
            buffer[i + 8] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;
    }
}
