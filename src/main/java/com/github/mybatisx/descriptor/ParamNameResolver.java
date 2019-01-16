package com.github.mybatisx.descriptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

public class ParamNameResolver {

    private static final String PARAMETER_CLASS = "java.lang.reflect.Parameter";
    private static Method GET_NAME;
    private static Method GET_PARAMS;

    static {
        try {
            Class<?> paramClass = Class.forName(PARAMETER_CLASS);
            GET_NAME = paramClass.getMethod("getName");
            GET_PARAMS = Method.class.getMethod("getParameters");
        } catch (Exception e) {
            // ignore
        }
    }
    private static final String SPRING4_DISCOVERER = "org.springframework.core.DefaultParameterNameDiscoverer";

    private static ParameterNameDiscoverer parameterNameDiscoverer() {
        ParameterNameDiscoverer discoverer;
        try {
            discoverer = (ParameterNameDiscoverer) Class.forName(SPRING4_DISCOVERER).newInstance();
        } catch (Exception e) {
            discoverer = new LocalVariableTableParameterNameDiscoverer();
        }
        return discoverer;
    }
    //private final ParameterNameDiscoverer parameterNameDiscover = parameterNameDiscoverer();

    public static String getActualParamName(Method method, int paramIndex) {


        if (GET_PARAMS == null) {
            return null;
        }
        try {
            Object[] params = (Object[]) GET_PARAMS.invoke(method);
            return (String) GET_NAME.invoke(params[paramIndex]);
        } catch (Exception e) {
            throw new IllegalStateException("Error occurred when invoking Method#getParameters().", e);
        }
    }

}
