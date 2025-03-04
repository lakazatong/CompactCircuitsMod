package net.lakazatong.compactcircuitsmod.utils;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class Utils {
    public static boolean isValidNumber(String s) {
        return isValidNumber(s, String.valueOf(Integer.MAX_VALUE).length(), Integer.MAX_VALUE);
    }

    public static boolean isValidNumber(String s, int maxLength) {
        return isValidNumber(s, maxLength, Integer.MAX_VALUE);
    }

    public static boolean isValidNumber(String s, int maxLength, Integer maxValue) {
        if (!s.matches(String.format("\\d{0,%d}", maxLength))) return false;
        if (maxValue == null) maxValue = Integer.MAX_VALUE;
        String maxValueStr = String.valueOf(maxValue);
        if (s.length() > maxValueStr.length()) return false;
        return s.length() != maxValueStr.length() || s.compareTo(maxValueStr) <= 0;
    }

    // considering length as the number of characters
    // the + 2 is to compensate for the different paddings and margins
    public static int getFieldWidth(Font font, int length) {
        return font.width("0") * (length + 2);
    }

    // inverse operation
    public static int getFieldLength(Font font, int width) {
        return width / font.width("0") - 2;
    }

    public static Component ctranslate(String namespace, String key) { return Component.translatable(namespace + "." + CompactCircuitsMod.MOD_ID + "." + key); }
    public static String translate(String namespace, String key) { return Component.translatable(namespace + "." + CompactCircuitsMod.MOD_ID + "." + key).getString(); }

    public static boolean keyPressed(int pKeyCode, Runnable onDone, Runnable onCancel) {
        if (pKeyCode == 257) {
            onDone.run();
            return true;
        }

        if (pKeyCode == 1) {
            onCancel.run();
            return true;
        }

        return false;
    }
}
