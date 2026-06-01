package com.forex.common.base.constant;

public final class SystemConstants {

    private SystemConstants() {
    }

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_NAME_HEADER = "X-User-Name";
    public static final String USER_ROLES_HEADER = "X-User-Roles";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String MAX_PAGE_SIZE = "200";

    public static final int BATCH_INSERT_SIZE = 500;
    public static final int BATCH_UPDATE_SIZE = 1000;
}
