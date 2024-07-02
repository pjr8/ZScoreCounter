package me.paulrobinson;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    private static final Executor threadPool = Executors.newFixedThreadPool(10);
    private static final AtomicLong overallCount = new AtomicLong();
    private static final long zScore = 7;
    private static boolean keepRunning = true;
    private static final NumberFormat nf = NumberFormat.getInstance(Locale.US);
    private static int thrice;

    public static void main(String[] args) {
        System.out.println("Looking for: " + zScore);
        for (int i = 0 ; i < 100 ; i++) {
            threadPool.execute(new RunRNG());
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!keepRunning) {
                if (thrice < 3) {
                    thrice++;
                    System.out.println("Final count: " + nf.format(overallCount.get()));
                }
            } else {
                System.out.println("Count: " + nf.format(overallCount.get()));
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    public static class RunRNG implements Runnable {

        @Override
        public void run() {
            Random random = new Random();
            long count = 0;
            while (keepRunning) {
                count++;
                double number = random.nextGaussian();
                if (number > zScore || number < -zScore) {
                    System.out.println("Random number: " + number + " | Count: " + Main.overallCount.getAndAdd(count));
                    keepRunning = false;
                }
                if (count == 1_00_000_000) {
                    Main.overallCount.addAndGet(1_00_000_000);
                    count = 0;
                }
            }
            Main.overallCount.getAndAdd(count);
        }
    }
}