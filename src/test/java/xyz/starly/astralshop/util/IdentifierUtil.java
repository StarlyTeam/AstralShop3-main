package xyz.starly.astralshop.util;

import java.util.UUID;

public class IdentifierUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
