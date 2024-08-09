package org.tetris.common.util.concurrency;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {
    private final ExecutorService executorService;

    public CommandExecutor(int numberOfThreads) {
        if (numberOfThreads <= 0) {
            executorService = Executors.newFixedThreadPool(1);
        } else {
            executorService = Executors.newFixedThreadPool(numberOfThreads);
        }
    }

    public void execute(List<Command> commands) {
        commands.forEach(command -> executorService.submit(command::execute));
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

    public boolean isRunning() {
        return executorService.isTerminated();
    }

    public boolean awaitTermination() {
        try {
            return executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}