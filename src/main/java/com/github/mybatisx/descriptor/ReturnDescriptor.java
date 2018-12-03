package com.github.mybatisx.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class ReturnDescriptor extends TypeWithAnnotationDescriptor {

    private ReturnDescriptor(Type type, List<Annotation> annotations) {
        super(type, annotations);
    }

    public static ReturnDescriptor create(Type type, List<Annotation> annotations) {
        return new ReturnDescriptor(type, annotations);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReturnDescriptor other = (ReturnDescriptor) obj;
        return Objects.equal(this.getType(), other.getType())
                && Objects.equal(this.getAnnotations(), other.getAnnotations());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType(), getAnnotations());
    }

}