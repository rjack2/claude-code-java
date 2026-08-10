package com.vinsguru.netflux.movie.util;

import java.util.Objects;

public final class LimitUtil {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;

    private LimitUtil() {
    }

    public static int clamp(Integer limit) {
        return Objects.isNull(limit) ? DEFAULT_LIMIT : Math.clamp(limit, MIN_LIMIT, MAX_LIMIT);
    }

}
