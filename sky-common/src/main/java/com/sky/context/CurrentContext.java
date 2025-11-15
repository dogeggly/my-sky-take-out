package com.sky.context;

public class CurrentContext {
    private static final ThreadLocal<Long> CURRENT_LOCAL = new ThreadLocal<>();
    public static void setCurrent(Long empId) {
        CURRENT_LOCAL.set(empId);
    }
    public static Long getCurrent() {
        return CURRENT_LOCAL.get();
    }
    public static void remove() {
        CURRENT_LOCAL.remove();
    }
}
