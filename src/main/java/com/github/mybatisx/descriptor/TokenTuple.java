package com.github.mybatisx.descriptor;

public class TokenTuple {

    private final TypeToken<?> first;
    private final TypeToken<?> second;

    public TokenTuple(TypeToken<?> first, TypeToken<?> second) {
        this.first = first;
        this.second = second;
    }

    public TypeToken<?> getFirst() {
        return first;
    }

    public TypeToken<?> getSecond() {
        return second;
    }

}
