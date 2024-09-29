package com.example.ratelimiterservice.ratelimiter;

import com.example.ratelimiterservice.bucket.Bucket;
import com.example.ratelimiterservice.bucket.LeakyBucket; // Assuming you have the LeakyBucket class

import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity; // Capacity of the leaky bucket
    private final int leakRate; // Leak rate in requests per second
    private final ConcurrentHashMap<String, Bucket> buckets;

    public LeakyBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        // Compute or retrieve the user's leaky bucket
        Bucket bucket = buckets.computeIfAbsent(userId, k -> new LeakyBucket(capacity, leakRate));
        return bucket.grant(); // Call the grant method to determine if the request is allowed
    }
}
