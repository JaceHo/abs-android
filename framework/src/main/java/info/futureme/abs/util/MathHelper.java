package info.futureme.abs.util;

public final class MathHelper {
    // Float Constants

    public static final float PI = 3.1415927f;

    // Float Arithmetic

    public static float sqr(float a) {
        return a * a;
    }

    public static float sumSqr(float a, float b) {
        return a * a + b * b;
    }

    public static double diag(float a, float b) {
        return Math.sqrt(a * a + b * b);
    }

    public static double exp(float value) {
        if (CompatHelper.sdk(17)) {
            return Math.exp(value);
        } else {
            return (float) Math.exp(value);
        }
    }

    public static double pow(float x, float y) {
        if (CompatHelper.sdk(17)) {
            return Math.pow(x, y);
        } else {
            return (float) Math.pow(x, y);
        }
    }

    public static float log(float a, float newBase) {
        if (Float.isNaN(a)) {
            return a; // IEEE 754-2008: NaN payload must be preserved
        }
        if (Float.isNaN(newBase)) {
            return newBase; // IEEE 754-2008: NaN payload must be preserved
        }

        if (newBase == 1f)
            return Float.NaN;
        if (a != 1 && (newBase == 0 || newBase == Float.POSITIVE_INFINITY))
            return Float.NaN;

        return (float) (Math.log(a) / Math.log(newBase));
    }

    // Range

    /**
     * Shrink the value to [min,max]
     */
    public static int range(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Shrink the value to [min,max]
     */
    public static float range(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * The value will be cycled in [min, max)
     */
    public static float cycle(float value, float min, float max) {
        if (value < min) {
            value = max - (min - value) % (max - min);
        }
        if (value >= max) {
            return min + (value - max) % (max - min);
        }
        return value;
    }

    /**
     * check value whether in [min,max]
     */
    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean inRange(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public static boolean isIntersected(int start1, int end1, int start2, int end2) {
        return start1 <= end2 && end1 >= start2;
    }

    public static boolean isIntersected(float start1, float end1, float start2, float end2) {
        return start1 <= end2 && end1 >= start2;
    }

    // Angle

    public static float radius(float degree) {
        return (degree / 180f) * PI;
    }

    public static float degree(float radius) {
        return (radius / PI) * 180f;
    }
}
