package com.github.mybatisx.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class ParameterDescriptor extends TypeWithAnnotationDescriptor {

    /**
     * 此参数在method参数列表中的位置，从0开始
     */
    private final int position;

    /**
     * 此参数在emthod参数列表中的名字
     */
    private final String name;

    private ParameterDescriptor(int position, Type type, List<Annotation> annotations, String name) {
        super(type, annotations);
        this.position = position;
        this.name = name;
    }

    public static ParameterDescriptor create(int position, Type type, List<Annotation> annotations, String name) {
        return new ParameterDescriptor(position, type, annotations, name);
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ParameterDescriptor other = (ParameterDescriptor) obj;
        return Objects.equal(this.position, other.position)
                && Objects.equal(this.name, other.name)
                && Objects.equal(this.getType(), other.getType())
                && Objects.equal(this.getAnnotations(), other.getAnnotations());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(position, name, getType(), getAnnotations());
    }

}