package com.padas2.bitbucket.supportzip;

import java.util.concurrent.*;

abstract public class TimeLimitedCodeBlock {
    private int duration;

    private TimeUnit timeUnit;

    public TimeLimitedCodeBlock(int duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                codeBlock();
            }
        });
        try {
            future.get(duration, timeUnit);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    abstract void codeBlock();
}
