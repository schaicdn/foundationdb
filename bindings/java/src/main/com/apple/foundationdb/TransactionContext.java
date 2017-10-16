/*
 * TransactionContext.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2013-2018 Apple Inc. and the FoundationDB project authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb;

import com.apple.foundationdb.async.Function;
import com.apple.foundationdb.async.Future;
import com.apple.foundationdb.async.PartialFunction;
import com.apple.foundationdb.async.PartialFuture;

import java.util.concurrent.Executor;

/**
 * A context in which a {@code Transaction} is available for database operations. The
 *  behavior of the methods specified in this interface, especially in the face
 *  errors, is implementation specific. In particular, some implementations will
 *  run {@link Function}s multiple times (retry) when certain errors are encountered.
 *  Therefore a {@code Function} should be prepared to be called more than once. This
 *  consideration means that a {@code Function} should use caution when directly
 *  modifying state in a class, especially in a way that could be observed were that
 *  {@code Function} to not complete successfully.
 */
public interface TransactionContext extends ReadTransactionContext {
	/**
	 * Runs a function in this context that takes a transaction. Depending on the type of
	 *  context, this may execute the supplied function multiple times if an error is
	 *  encountered. This method is blocking -- control will not return from this call
	 *  until work is complete.
	 *
	 * @param retryable the block of logic to execute against a {@link Transaction}
	 *  in this context
	 *
	 * @return a result of the last call to {@code retryable}
	 */
	public abstract <T> T run(Function<? super Transaction, T> retryable);

	/**
	 * Runs a function in this context that takes a transaction. Use this formulation of
	 *  {@link #run(Function)} if the called user code throws checked exceptions.
	 *
	 * @param retryable the block of logic to execute against a {@link Transaction}
	 *  in this context
	 *
	 * @return a result of the last call to {@code retryable}
	 *
	 * @see #run(Function)
	 */
	public abstract <T> T run(PartialFunction<? super Transaction, T> retryable) throws Exception;

	/**
	 * Runs a function in this context that takes a transaction. Depending on the type of
	 *  context, this may execute the supplied function multiple times if an error is
	 *  encountered. This call is non-blocking -- control flow will return immediately
	 *  with a {@code Future} that will be set when the process is complete.
	 *
	 * @param retryable the block of logic to execute against a {@link Transaction}
	 *  in this context
	 *
	 * @return a {@code Future} that will be set to the value returned by the last call
	 *  to {@code retryable}
	 */
	public abstract <T> Future<T> runAsync(
			Function<? super Transaction, Future<T>> retryable);

	/**
	 * Runs a function in this context that takes a transaction. Use this formulation of
	 *  {@link #runAsync(Function)} if the called user code throws checked exceptions.
	 *
	 * @param retryable the block of logic to execute against a {@link Transaction}
	 *  in this context
	 *
	 * @return a {@code PartialFuture} that will be set to the value returned by the last call
	 *  to {@code retryable}
	 */
	public abstract <T> PartialFuture<T> runAsync(
			PartialFunction<? super Transaction, ? extends PartialFuture<T>> retryable);

	/**
	 * Retrieves the {@link Executor} used by this {@code TransactionContext} when running
	 * asynchronous callbacks.
	 *
	 * @return the {@link Executor} used by this {@code TransactionContext}
	 */
	public abstract Executor getExecutor();

}