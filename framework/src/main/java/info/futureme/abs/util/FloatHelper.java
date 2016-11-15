package info.futureme.abs.util;

/**
 * Helper to solve precision problem of single-precision floatint-point
 * format.
 */
public class FloatHelper {
    private static final float DEFAULT_PRECISION = 1e-03f;

    // Base

    public static boolean eq(float a, float b) {
        return eq(a, b, DEFAULT_PRECISION);
    }

    public static boolean eq(float a, float b, float precision) {
        return Math.abs(a - b) < precision;
    }

    public static boolean gt(float a, float b) {
        return gt(a, b, DEFAULT_PRECISION);
    }

    public static boolean gt(float a, float b, float precision) {
        return a - b > precision;
    }

    public static boolean ge(float a, float b) {
        return ge(a, b, DEFAULT_PRECISION);
    }

    public static boolean ge(float a, float b, float precision) {
        return a - b > -precision;
    }

    public static boolean lt(float a, float b) {
        return lt(a, b, DEFAULT_PRECISION);
    }

    public static boolean lt(float a, float b, float precision) {
        return b - a > precision;
    }

    public static boolean le(float a, float b) {
        return le(a, b, DEFAULT_PRECISION);
    }

    public static boolean le(float a, float b, float precision) {
        return b - a > -precision;
    }

    public static int floor(float value) {
        return floor(value, DEFAULT_PRECISION);
    }

    public static int floor(float value, float precision) {
        int ret = (int) Math.floor(value);
        return 1f - (value - ret) < precision ? (ret + 1) : ret;
    }

    public static int ceil(float value) {
        return ceil(value, DEFAULT_PRECISION);
    }

    public static int ceil(float value, float precision) {
        int ret = (int) Math.ceil(value);
        return 1f - (ret - value) < precision ? (ret - 1) : ret;
    }

    public static float minus(float a, float b) {
        return minus(a, b, DEFAULT_PRECISION);
    }

    public static float minus(float a, float b, float precision) {
        float ret = a - b;
        return ret > -precision && ret < precision ? 0f : ret;
    }

    public static float plus(float a, float b) {
        return plus(a, b, DEFAULT_PRECISION);
    }

    public static float plus(float a, float b, float precision) {
        float ret = a + b;
        return ret > -precision && ret < precision ? 0f : ret;
    }

    // Extensions

    public static boolean inRange(float value, float min, float max) {
        return inRange(value, min, max, DEFAULT_PRECISION);
    }

    public static boolean inRange(float value, float min, float max, float precision) {
        return (value - min > -precision) && (max - value > -precision);
    }

    public static float range(float value, float min, float max) {
        return range(value, min, max, DEFAULT_PRECISION);
    }

    public static float range(float value, float min, float max, float precision) {
        if (le(value, min, precision)) {
            return min;
        } else if (ge(value, max, precision)) {
            return max;
        }
        return value;
    }

    public static float cycle(float value, float min, float max) {
        return cycle(value, min, max, DEFAULT_PRECISION);
    }

    public static float cycle(float value, float min, float max, float precision) {
        if (lt(value, min, precision)) {
            return max - minus(min, value) % (max - min);
        } else if (ge(value, max, precision)) {
            return min + minus(value, max) % (max - min);
        }
        return value;
    }
}
