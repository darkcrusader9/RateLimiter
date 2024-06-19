package com.example.ratelimiterservice.ratelimiter;

import com.example.ratelimiterservice.bucket.Bucket;
import com.example.ratelimiterservice.bucket.LeakyBucket;
import com.example.ratelimiterservice.ratelimiter.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LeakyBucketRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long intervalMillis;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(int maxRequests, long intervalMillis) {
        this.maxRequests = maxRequests;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public boolean allowRequest(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, k -> new LeakyBucket(maxRequests, intervalMillis));
        return bucket.grant();
    }
}