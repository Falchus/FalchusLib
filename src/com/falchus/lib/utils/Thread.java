package com.falchus.lib.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for running tasks.
 */
@UtilityClass
public class Thread {

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
	
	/**
	 * Runs the given task synchronously.
	 */
	public static void run(@NonNull Runnable task) {
        task.run();
	}
	
	/**
	 * Runs the given task asynchronously.
	 */
    public static void runAsync(@NonNull Runnable task) {
        CompletableFuture.runAsync(task, EXECUTOR);
    }
}
