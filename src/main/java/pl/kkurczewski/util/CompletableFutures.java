package pl.kkurczewski.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class CompletableFutures {
    public static <T> CompletableFuture<T> completableFuture(Future<T> future) {
        var completableFuture = new CompletableFuture<T>();
        ForkJoinPool.commonPool().execute(() -> {
            try {
                completableFuture.complete(future.get());
            } catch (InterruptedException | ExecutionException e) {
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }
}
