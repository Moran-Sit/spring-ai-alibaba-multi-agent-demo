package com.alibaba.cloud.ai.demo.common.rag;


import org.apache.commons.lang3.StringUtils;

public class TokenEstimatorThreadLocal {
    private static final ThreadLocal<String> tokenEstimator = new ThreadLocal<>();

    public static void setTokenEstimator(String value) {
        tokenEstimator.set(StringUtils.isBlank(value) ? "" : value);
    }

    public static String getTokenEstimator() {
        return tokenEstimator.get();
    }

    public static void clearTokenEstimator() {
        tokenEstimator.remove();
    }
}
