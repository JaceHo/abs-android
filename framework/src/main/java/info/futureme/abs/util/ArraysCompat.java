package info.futureme.abs.util;

import java.util.Arrays;

public class ArraysCompat {
    public static int binarySearch(int[] array, int startIndex, int endIndex, int value) {
        if (CompatHelper.sdk(9)) {
            return Arrays.binarySearch(array, startIndex, endIndex, value);
        } else {
            checkBinarySearchBounds(startIndex, endIndex, array.length);
            int lo = startIndex;
            int hi = endIndex - 1;

            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                int midVal = array[mid];

                if (midVal < value)
                    lo = mid + 1;
                else if (midVal > value)
                    hi = mid - 1;
                else
                    return mid; // value found
            }
            return ~lo; // value not present
        }
    }

    private static void checkBinarySearchBounds(int startIndex, int endIndex, int length) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException();
        }
        if (startIndex < 0 || endIndex > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
