package com.example.ratelimiterservice.factory;

import com.example.ratelimiterservice.ratelimiter.FixedWindowRateLimiter;
import com.example.ratelimiterservice.ratelimiter.LeakyBucketRateLimiter;
import com.example.ratelimiterservice.ratelimiter.RateLimiter;
import com.example.ratelimiterservice.ratelimiter.TokenBucketRateLimiter;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(String type, int maxRequests, long intervalMillis) {
        switch (type) {
            case "TokenBucket":
                return new TokenBucketRateLimiter(maxRequests, maxRequests / 2, intervalMillis);
            case "LeakyBucket":
                return new LeakyBucketRateLimiter(maxRequests, intervalMillis);
            case "FixedWindow":
                return new FixedWindowRateLimiter(intervalMillis, maxRequests);
            default:
                throw new IllegalArgumentException("Invalid rate limiter type");
        }
    }
}
