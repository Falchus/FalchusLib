package com.falchus.lib.interfaces.consumer;

import java.util.function.BiConsumer;

/**
 * A functional interface similar to {@link BiConsumer},
 * but accepts three input arguments instead of two.
 * 
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @param <V> the type of the third argument
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	
	/**
	 * Performs this operation on the given arguments.
	 * 
	 * @param t the first input argument
	 * @param u the second input argument
	 * @param v the third input argument
	 */
	void accept(T t, U u, V v);
}
