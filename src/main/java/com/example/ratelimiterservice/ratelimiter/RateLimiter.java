package com.example.ratelimiterservice.ratelimiter;

public interface RateLimiter {
    boolean allowRequest(String userId);
}
