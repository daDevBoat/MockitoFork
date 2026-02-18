/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayEquals extends Equals {

    public ArrayEquals(Object wanted) {
        super(wanted);
    }

    @Override
    public boolean matches(Object actual) {
        Object wanted = getWanted();
        if (wanted == null || actual == null) { // +1 +1 = +2
            return super.matches(actual); // -1
        }
        Boolean primitiveResult = matchesPrimitiveArray(wanted, actual);
        if(primitiveResult != null) { // +1
            return primitiveResult; // -1
        }
        if(wanted instanceof Object[] && actual instanceof Object[]) { // +1 +1 = +2
            return Arrays.equals((Object[]) wanted, (Object[]) actual); // -1
        }
        return false; // -1
    }

    // π = 5
    // s = 4
    // M = π - s + 2 = 3

    private Boolean matchesPrimitiveArray(Object wanted, Object actual) {
        if (wanted instanceof boolean[] && actual instanceof boolean[]) {
            return Arrays.equals((boolean[]) wanted, (boolean[]) actual);
        } else if (wanted instanceof byte[] && actual instanceof byte[]) {
            return Arrays.equals((byte[]) wanted, (byte[]) actual);
        } else if (wanted instanceof char[] && actual instanceof char[]) {
            return Arrays.equals((char[]) wanted, (char[]) actual);
        } else if (wanted instanceof double[] && actual instanceof double[]) {
            return Arrays.equals((double[]) wanted, (double[]) actual);
        } else if (wanted instanceof float[] && actual instanceof float[]) {
            return Arrays.equals((float[]) wanted, (float[]) actual);
        } else if (wanted instanceof int[] && actual instanceof int[]) {
            return Arrays.equals((int[]) wanted, (int[]) actual);
        } else if (wanted instanceof long[] && actual instanceof long[]) {
            return Arrays.equals((long[]) wanted, (long[]) actual);
        } else if (wanted instanceof short[] && actual instanceof short[]) {
            return Arrays.equals((short[]) wanted, (short[]) actual);
        }
        return null;
    } 

    @Override
    public String toString() {
        if (getWanted() != null && getWanted().getClass().isArray()) {
            return appendArray(createObjectArray(getWanted()));
        } else {
            return super.toString();
        }
    }

    private String appendArray(Object[] array) {
        // TODO SF overlap with ValuePrinter
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            out.append(new Equals(array[i]));
            if (i != array.length - 1) {
                out.append(", ");
            }
        }
        out.append("]");
        return out.toString();
    }

    public static Object[] createObjectArray(Object array) {
        if (array instanceof Object[]) {
            return (Object[]) array;
        }
        Object[] result = new Object[Array.getLength(array)];
        for (int i = 0; i < Array.getLength(array); i++) {
            result[i] = Array.get(array, i);
        }
        return result;
    }
}
