package com.example.ratelimiterservice.ratelimiter;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLogRateLimiter implements RateLimiter{
    private final long windowSizeMillis;
    private final int maxRequests;
    private ConcurrentLinkedDeque<Long> timestamps;
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowLogRateLimiter(long windowSizeMillis, int maxRequests) {
        this.windowSizeMillis = windowSizeMillis;
        this.maxRequests = maxRequests;
        this.timestamps = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        lock.lock();
        try{
            long currentTime = System.currentTimeMillis();
            while(!timestamps.isEmpty() && (currentTime - timestamps.peekFirst()) > windowSizeMillis){
                timestamps.pollFirst();
            }
            if(timestamps.size() < maxRequests){
                timestamps.addLast(currentTime);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
