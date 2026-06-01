package com.forex.common.base.constant;

public final class CacheConstants {

    private CacheConstants() {
    }

    public static final String RATE_PREFIX = "rate:";
    public static final String LOCK_PREFIX = "lock:";
    public static final String IDEMPOTENT_PREFIX = "idempotent:";
    public static final String RATE_LIMIT_PREFIX = "rateLimit:";
    public static final String QUOTA_PREFIX = "quota:";
    public static final String BLACKLIST_KEY = "blacklist:full";
    public static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    public static final String CONFIG_PREFIX = "config:";

    public static final long RATE_CACHE_TTL_SECONDS = 10;
    public static final long LOCK_DEFAULT_TTL_SECONDS = 10;
    public static final long IDEMPOTENT_DEFAULT_TTL_SECONDS = 30;
    public static final long BLACKLIST_REFRESH_MINUTES = 5;
}
