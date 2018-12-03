package com.github.mybatisx.descriptor;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public abstract class TypeParameter<T> extends TypeCapture<T> {

    final TypeVariable<?> typeVariable;

    protected TypeParameter() {
        Type type = capture();
        if (!(type instanceof TypeVariable)) {
            throw new IllegalArgumentException("type should be a type variable, but " + type);
        }
        this.typeVariable = (TypeVariable<?>) type;
    }

    @Override public final int hashCode() {
        return typeVariable.hashCode();
    }

    @Override public final boolean equals(@Nullable Object o) {
        if (o instanceof TypeParameter) {
            TypeParameter<?> that = (TypeParameter<?>) o;
            return typeVariable.equals(that.typeVariable);
        }
        return false;
    }

    @Override public String toString() {
        return typeVariable.toString();
    }
}