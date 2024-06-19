package com.example.ratelimiterservice.bucket;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucket implements Bucket {
    private final int maxRequests;
    private final long intervalMillis;
    private long lastRequestTime;
    private int requestCount;
    private final ReentrantLock lock = new ReentrantLock();

    public LeakyBucket(int maxRequests, long intervalMillis) {
        this.maxRequests = maxRequests;
        this.intervalMillis = intervalMillis;
        this.lastRequestTime = System.currentTimeMillis();
        this.requestCount = 0;
    }

    @Override
    public boolean grant() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            System.out.println("Request sent to leaky bucket at " + currentTime);
            if (currentTime - lastRequestTime >= intervalMillis) {
                // Reset the bucket
                lastRequestTime = currentTime;
                requestCount = 0;
            }
            if (requestCount < maxRequests) {
                requestCount++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
