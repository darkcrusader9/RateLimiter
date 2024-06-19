package com.example.ratelimiterservice.ratelimiter;

public class SlidingWindowCounterRateLimiter implements RateLimiter{
    @Override
    public boolean allowRequest(String userId) {
        return false;
    }
}
