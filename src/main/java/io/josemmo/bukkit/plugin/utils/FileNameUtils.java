package io.josemmo.bukkit.plugin.utils;

public class FileNameUtils {

    public static String removeExtension(final String fileName) {
        return fileName != null && fileName.lastIndexOf(".") > 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
    }
}
