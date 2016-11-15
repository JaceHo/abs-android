package info.futureme.abs.util;

/**
 * Helper to convert units
 */
public class UnitHelper {
    // Space Size

    public static final int BYTES_PER_KB = 1000;
    public static final int BYTES_PER_MB = 1000 * 1000;
    public static final int BYTES_PER_GB = 1000 * 1000 * 1000;

    public static String toSpaceString(int byteCount) {
        if (byteCount < BYTES_PER_KB) {
            return String.valueOf(byteCount) + "B";
        } else if (byteCount < BYTES_PER_MB) {
            return String.valueOf(byteCount / BYTES_PER_KB) + "KB";
        } else if (byteCount < BYTES_PER_GB) {
            return String.format("%.2f", (float) byteCount / BYTES_PER_MB) + "M";
        } else {
            return String.format("%.2f", (float) byteCount / BYTES_PER_GB) + "G";
        }
    }
}
