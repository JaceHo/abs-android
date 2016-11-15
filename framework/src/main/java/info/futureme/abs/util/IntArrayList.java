package info.futureme.abs.util;

import java.util.Arrays;

public class IntArrayList {
    /**
     * The minimum amount by which the capacity of an IntArrayList will
     * increase. This tuning parameter controls a time-space tradeoff. This
     * value (12) gives empirically good results and is arguably consistent with
     * the RI's specified default initial capacity of 10: instead of 10, we
     * start with 0 (sans allocation) and jump to 12.
     */
    private static final int MIN_CAPACITY_INCREMENT = 12;

    /**
     * The number of elements in this list.
     */
    private int size;

    private int[] array;

    public IntArrayList() {
        this(MIN_CAPACITY_INCREMENT);
    }

    public IntArrayList(int capacity) {
        array = new int[capacity];
    }

    public IntArrayList(IntArrayList src) {
        this(src.size());
        System.arraycopy(src.array, 0, array, 0, array.length);
    }

    /**
     * Adds the specified object at the end of this {@code IntArrayList}.
     *
     * @param int the value to add.
     * @return always true
     */
    public IntArrayList add(int value) {
        int[] a = array;
        int s = size;
        if (s == a.length) {
            int[] newArray = new int[s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1)];
            System.arraycopy(a, 0, newArray, 0, s);
            array = a = newArray;
        }
        a[s] = value;
        size = s + 1;
        return this;
    }

    /**
     * Inserts the specified value into this {@code IntArrayList} at the
     * specified location. The value is inserted before any previous element at
     * the specified location. If the location is equal to the size of this
     * {@code IntArrayList}, the value is added at the end.
     *
     * @param index the index at which to insert the object.
     * @param value the value to add.
     * @throws IndexOutOfBoundsException when {@code location < 0 || location > size()}
     */
    public IntArrayList add(int index, int value) {
        int[] a = array;
        int s = size;
        if (index > s || index < 0) {
            throwIndexOutOfBoundsException(index, s);
        }

        if (s < a.length) {
            System.arraycopy(a, index, a, index + 1, s - index);
        } else {
            // assert s == a.length;
            int[] newArray = new int[newCapacity(s)];
            System.arraycopy(a, 0, newArray, 0, index);
            System.arraycopy(a, index, newArray, index + 1, s - index);
            array = a = newArray;
        }
        a[index] = value;
        size = s + 1;
        return this;
    }

    public int binaryAdd(int value) {
        int index = ArraysCompat.binarySearch(array, 0, size, value);
        if (index < 0) {
            index = ~index;
        }
        add(index, value);
        return index;
    }

    /**
     * @return the non-negative index of the element, or a negative index which
     * is -index - 1 where the element would be inserted.
     */
    public int binarySearch(int value) {
        return ArraysCompat.binarySearch(array, 0, size, value);
    }

    /**
     * @return positive index of the value to insert
     */
    public int binaryInsertSearch(int value) {
        int index = ArraysCompat.binarySearch(array, 0, size, value);
        if (index < 0) {
            index = -index - 1;
        }
        return index;
    }

    public boolean binaryContains(int value) {
        return ArraysCompat.binarySearch(array, 0, size, value) >= 0;
    }

    public void binaryRemove(int value) {
        int index = ArraysCompat.binarySearch(array, 0, size, value);
        if (index >= 0) {
            removeAt(index);
        }
    }

    /**
     * Removes all elements from this {@code ArrayList}, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    public void clear() {
        if (size != 0) {
            size = 0;
        }
    }

    public int get(int index) {
        if (index >= size) {
            throwIndexOutOfBoundsException(index, size);
        }
        return array[index];
    }

    /**
     * Returns the number of elements in this {@code ArrayList}.
     *
     * @return the number of elements in this {@code ArrayList}.
     */
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Searches this {@code ArrayList} for the specified object.
     *
     * @param value the value to search for.
     * @return {@code true} if {@code object} is an element of this
     * {@code IntArrayList}, {@code false} otherwise
     */
    public boolean contains(int value) {
        int[] a = array;
        int s = size;
        for (int i = 0; i < s; i++) {
            if (value == a[i]) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(int value) {
        int[] a = array;
        int s = size;
        for (int i = 0; i < s; i++) {
            if (value == a[i]) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(int value) {
        int[] a = array;
        for (int i = size - 1; i >= 0; i--) {
            if (value == a[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes the object at the specified location from this list.
     *
     * @param index the index of the object to remove.
     * @return the removed object.
     * @throws IndexOutOfBoundsException when {@code location < 0 || location >= size()}
     */
    public int removeAt(int index) {
        int[] a = array;
        int s = size;
        if (index >= s) {
            throwIndexOutOfBoundsException(index, s);
        }
        int result = a[index];
        System.arraycopy(a, index + 1, a, index, --s - index);
        size = s;
        return result;
    }

    public boolean remove(int value) {
        int[] a = array;
        int s = size;
        for (int i = 0; i < s; i++) {
            if (value == a[i]) {
                System.arraycopy(a, i + 1, a, i, --s - i);
                size = s;
                return true;
            }
        }
        return false;
    }

    public void removeRange(int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return;
        }
        int[] a = array;
        int s = size;
        if (startIndex >= s) {
            throw new IndexOutOfBoundsException("fromIndex " + startIndex + " >= size " + size);
        }
        if (endIndex > s) {
            throw new IndexOutOfBoundsException("toIndex " + endIndex + " > size " + size);
        }
        if (startIndex > endIndex) {
            throw new IndexOutOfBoundsException("fromIndex " + startIndex + " > toIndex " + endIndex);
        }

        System.arraycopy(a, endIndex, a, startIndex, s - endIndex);
        int rangeSize = endIndex - startIndex;
        size = s - rangeSize;
    }

    /**
     * Replaces the element at the specified location in this {@code ArrayList}
     * with the specified object.
     *
     * @param index  the index at which to put the specified object.
     * @param object the object to add.
     * @return the previous element at the index.
     * @throws IndexOutOfBoundsException when {@code location < 0 || location >= size()}
     */
    public void set(int index, int value) {
        if (index >= size) {
            throwIndexOutOfBoundsException(index, size);
        }
        array[index] = value;
    }

    public void offset(int index, int valueChange) {
        if (index >= size) {
            throwIndexOutOfBoundsException(index, size);
        }
        array[index] = array[index] + valueChange;
    }

    /**
     * Returns a new array containing all elements contained in this
     * {@code ArrayList}.
     *
     * @return an array of the elements from this {@code ArrayList}
     */
    public int[] toArray() {
        int s = size;
        int[] result = new int[s];
        System.arraycopy(array, 0, result, 0, s);
        return result;
    }

    /**
     * Sets the capacity of this {@code ArrayList} to be the same as the current
     * size.
     *
     * @see #size
     */
    public void trimToSize() {
        int s = size;
        if (s == array.length) {
            return;
        }
        if (s > 0) {
            int[] newArray = new int[s];
            System.arraycopy(array, 0, newArray, 0, s);
            array = newArray;
        }
    }

    public void sort() {
        Arrays.sort(array, 0, size);
    }

    @Override
    public String toString() {
        String str = "<" + size + ">: ";
        for (int i = 0, end = Math.min(size, 1000); i < end; ++i) {
            str += array[i] + ", ";
        }
        return str;
    }

    /**
     * This method was extracted to encourage VM to inline callers. TODO: when
     * we have a VM that can actually inline, move the test in here too!
     */
    private static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }

    /**
     * This method controls the growth of ArrayList capacities. It represents a
     * time-space tradeoff: we don't want to grow lists too frequently (which
     * wastes time and fragments storage), but we don't want to waste too much
     * space in unused excess capacity.
     * <p/>
     * NOTE: This method is inlined into {@link #add(Object)} for performance.
     * If you change the method, change it there too!
     */
    private static int newCapacity(int currentCapacity) {
        int increment = (currentCapacity < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : currentCapacity >> 1);
        return currentCapacity + increment;
    }
}
