package com.forex.common.security.util;

import java.util.List;

public class UserContextHolder {

    private static final ThreadLocal<UserInfo> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserInfo userInfo) {
        CONTEXT.set(userInfo);
    }

    public static UserInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static Long getUserId() {
        UserInfo info = get();
        return info != null ? info.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo info = get();
        return info != null ? info.getUsername() : null;
    }

    public static List<String> getRoles() {
        UserInfo info = get();
        return info != null ? info.getRoles() : List.of();
    }

    public static List<String> getPermissions() {
        UserInfo info = get();
        return info != null ? info.getPermissions() : List.of();
    }
}
