package com.example.ratelimiterservice.bucket;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucket implements Bucket {
    private final int capacity; // Capacity of the bucket
    private final int leakRate; // Rate at which the bucket "leaks" (processed requests per second)
    private AtomicInteger currentWaterLevel; // Current amount of water in the bucket (queued requests)
    private long lastLeakTime; // Last time the bucket leaked
    private final ReentrantLock lock = new ReentrantLock(); // Lock for thread safety

    public LeakyBucket(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.currentWaterLevel = new AtomicInteger(0);
        this.lastLeakTime = System.nanoTime();
    }

    @Override
    public boolean grant() {
        lock.lock(); // Acquire the lock
        try {
            leakWater();
            if (currentWaterLevel.get() < capacity) {
                currentWaterLevel.incrementAndGet(); // Add a request to the bucket
                return true; // Grant permission for the request
            }
            return false; // Deny request if the bucket is full
        } finally {
            lock.unlock(); // Ensure the lock is released
        }
    }

    private void leakWater() {
        long currentTime = System.nanoTime();
        long timeSinceLastLeak = currentTime - lastLeakTime;

        // Calculate how much water should have leaked
        int waterToLeak = (int) (timeSinceLastLeak / 1_000_000_000L * leakRate);

        if (waterToLeak > 0) {
            int newWaterLevel = Math.max(currentWaterLevel.get() - waterToLeak, 0);
            currentWaterLevel.set(newWaterLevel); // Update the current water level
            lastLeakTime = currentTime; // Update the last leak time
        }
    }
}
