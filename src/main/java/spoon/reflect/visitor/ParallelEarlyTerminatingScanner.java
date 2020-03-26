/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * ParallelEarlyTerminatingScanner allows using multiple threads for concurrent
 * scanning with {@link EarlyTerminatingScanner}.
 *
 * <b> This class should only be used if all scanners do the same.</b> Otherwise
 * the result may vary from the expected result. All scanners <b> must </b>
 * synchronize shared fields like Collections by themselves.
 *
 * For creating and managing threads a {@link Executors#newFixedThreadPool()} is
 * used. Creating more threads then cores can harm the performance. Using a
 * different thread pool could increase the performance, but this class should
 * be general usage. If you need better performance you may want to use an own
 * class with different parallel approach.
 */

public class ParallelEarlyTerminatingScanner<T> extends EarlyTerminatingScanner<T> {

	private ExecutorService service;
	private ArrayBlockingQueue<EarlyTerminatingScanner<T>> scannerQueue;
	private AtomicBoolean isTerminated = new AtomicBoolean(false);

	/**
	 * Creates a new ParallelEarlyTerminatingScanner from given iterable. The
	 * iterable is fully consumed. Giving an endless iterable of
	 * EarlyTerminatingScanner will result in errors. The scanners must follow the
	 * guidelines given in the class description.
	 *
	 * @param scanners iterable of scanners.
	 * @throws IllegalArgumentException if size of iterable is less than 1.
	 *
	 */
	public ParallelEarlyTerminatingScanner(Iterable<? extends EarlyTerminatingScanner<T>> scanners) {
		// added cast because constructors need int
		int scannerNumber = (int) StreamSupport.stream(scanners.spliterator(), false).count();
		scannerQueue = new ArrayBlockingQueue<>(scannerNumber);
		scanners.forEach(scannerQueue::add);
		service = Executors.newFixedThreadPool(scannerNumber);
	}

	/**
	 * Creates a new ParallelEarlyTerminatingScanner from given iterable. The
	 * EarlyTerminatingScanner must follow the guidelines given in the class
	 * description.
	 *
	 * @param scanners         iterable of EarlyTerminatingScanner.
	 * @param numberOfScanners number consumed from the iterable added to the active
	 *                         scanners.
	 * @throws SpoonException           if iterable has less values then
	 *                                  numberOfScanners.
	 * @throws IllegalArgumentException if numberOfScanners is less than 1.
	 *
	 */
	public ParallelEarlyTerminatingScanner(Iterable<? extends EarlyTerminatingScanner<T>> scanners,
			int numberOfScanners) {
		scannerQueue = new ArrayBlockingQueue<>(numberOfScanners);
		service = Executors.newFixedThreadPool(numberOfScanners);
		Iterator<? extends EarlyTerminatingScanner<T>> it = scanners.iterator();
		for (int i = 0; i < numberOfScanners; i++) {
			if (!it.hasNext()) {
				throw new SpoonException("not enough elements provided, iterable is already empty");
			}
			scannerQueue.add(it.next());
		}
	}

	@Override
	protected void doScan(CtRole role, CtElement element, ScanningMode mode) {
		scannerFunction((scanner) -> scanner.doScan(role, element, mode));
	}

	@Override
	public void scan(CtRole role, Collection<? extends CtElement> elements) {
		scannerFunction((scanner) -> scanner.scan(role, elements));
	}

	@Override
	public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
		scannerFunction((scanner) -> scanner.scan(role, elements));
	}

	@Override
	public void scan(CtRole role, CtElement element) {
		scannerFunction((scanner) -> scanner.scan(role, element));
	}

	@Override
	public void scan(CtElement element) {
		scannerFunction((scanner) -> scanner.scan(element));

	}

	@Override
	public void scan(CtRole role, Object o) {
		scannerFunction((scanner) -> scanner.scan(role, o));
	}

	@Override
	public void scan(Collection<? extends CtElement> elements) {
		scannerFunction((scanner) -> scanner.scan(elements));
	}

	@Override
	public void scan(Object o) {
		scannerFunction((scanner) -> scanner.scan(o));
	}

	private void scannerFunction(Consumer<? super EarlyTerminatingScanner<T>> consumer) {
		try {
			EarlyTerminatingScanner<T> currentScanner = scannerQueue.take();
			service.execute(() -> {
				try {
					consumer.accept(currentScanner);
					if (currentScanner.isTerminated()) {
						scannerQueue.forEach(v -> v.terminate());
						isTerminated.set(true);
					}
					scannerQueue.put(currentScanner);
				} catch (InterruptedException e) {
					// because rethrow is not possible here.
					Thread.currentThread().interrupt();
					e.printStackTrace();
					scannerQueue.add(currentScanner);
				} catch (Exception e) {
					// allows throwing exception, but keeping the scanner in the queue
					scannerQueue.add(currentScanner);
					throw e;
				}
			});
		} catch (InterruptedException e) {
			// because rethrow is not possible here.
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	/**
	 * The return value of this method is <b> not</b> deterministic if multiple
	 * results could be found.
	 *
	 * @return the result of scanning - the value, which was stored by a previous
	 *         call of {@link #setResult(Object)}
	 */
	@Override
	public T getResult() {
		return scannerQueue.stream()
				.map(EarlyTerminatingScanner::getResult)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

	@Override
	protected boolean isTerminated() {
		return isTerminated.get();
	}

	@Override
	protected void terminate() {
		isTerminated.set(true);
		scannerQueue.forEach(v -> v.terminate());
	}

	@Override
	public EarlyTerminatingScanner<T> setListener(CtScannerListener listener) {
		scannerQueue.forEach(v -> v.setListener(listener));
		return this;
	}

	@Override
	public CtScannerListener getListener() {
		return scannerQueue.peek().getListener();
	}

	@Override
	public EarlyTerminatingScanner<T> setVisitCompilationUnitContent(boolean visitCompilationUnitContent) {
		scannerQueue.forEach(v -> v.setVisitCompilationUnitContent(visitCompilationUnitContent));
		return this;
	}

	@Override
	public boolean isVisitCompilationUnitContent() {
		return scannerQueue.peek().isVisitCompilationUnitContent();
	}

	/**
	 * Blocks until all tasks have completed execution, the timeout occurs, or the
	 * current thread is interrupted, whichever happens first. <b> Dont use this
	 * method with timeout 0<b>. Timeout 0 returns directly. Use
	 * {@link #awaitTermination} instead.
	 *
	 * @param timeout the maximum time to wait in milliseconds.
	 * @throws InterruptedException if interrupted while waiting
	 */
	public void awaitTermination(long timeout) throws InterruptedException {
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Blocks until all tasks have completed execution or the current thread is
	 * interrupted, whichever happens first.
	 *
	 * @throws InterruptedException if interrupted while waiting
	 */
	public void awaitTermination() throws InterruptedException {
		// JDK developers find the behavior timeout = 0 returns instant clearly
		// expected. We need this method, because normally you dont relay on timeouts.
		// see https://bugs.openjdk.java.net/browse/JDK-6179024 for more info.
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}
}
