package pl.project13.common.utils;

import java.io.File;

public class HumanReadable {

    public static String fileSize(File file) {
        return byteCount(file.length());
    }

    public static String byteCount(long bytes) {
        return byteCount(bytes, false);
    }

    public static String byteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
