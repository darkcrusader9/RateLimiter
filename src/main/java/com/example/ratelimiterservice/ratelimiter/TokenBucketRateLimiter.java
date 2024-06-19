package com.example.ratelimiterservice.ratelimiter;

import com.example.ratelimiterservice.bucket.Bucket;
import com.example.ratelimiterservice.bucket.TokenBucket;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter{
    private final int maxTokens;
    private final int refillTokens;
    private final long refillIntervalMillis;
    private final ConcurrentHashMap<String, Bucket> buckets;

    public TokenBucketRateLimiter(int maxTokens, int refillTokens, long refillIntervalMillis) {
        this.maxTokens = maxTokens;
        this.refillTokens = refillTokens;
        this.refillIntervalMillis = refillIntervalMillis;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, k -> new TokenBucket(maxTokens, refillTokens, refillIntervalMillis));
        return bucket.grant();
    }
}
