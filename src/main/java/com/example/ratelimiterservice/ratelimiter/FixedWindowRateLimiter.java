package com.example.ratelimiterservice.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowRateLimiter implements RateLimiter{
    private long windowSizeMillis;
    private int maxRequests;
    private long windowStart;
    private AtomicInteger count;
    private final ReentrantLock lock = new ReentrantLock();

    public FixedWindowRateLimiter(long windowSizeMillis, int maxRequests) {
        this.windowSizeMillis = windowSizeMillis;
        this.maxRequests = maxRequests;
        this.windowStart = System.currentTimeMillis();
        this.count = new AtomicInteger(0);
    }

    @Override
    public boolean allowRequest(String userId) {
        lock.lock();
        try{
            long currentTimestamp = System.currentTimeMillis();
            if(currentTimestamp - windowStart > windowStart){
                windowStart = currentTimestamp;
                count.set(0);
            }
            if(count.incrementAndGet() <= maxRequests){
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
