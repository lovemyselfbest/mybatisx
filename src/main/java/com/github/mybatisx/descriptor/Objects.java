package com.github.mybatisx.descriptor;

import javax.annotation.Nullable;

public class Objects {

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(@Nullable Object... objects) {
        return java.util.Arrays.hashCode(objects);
    }

}
