/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

/**
 * AbstractParallelProcessor allows using multiple threads for concurrent
 * processing with {@link AbstractProcessor}.
 *
 * <b> This class should only be used if all processors do the same.</b>
 * Otherwise the result may vary from the expected result. All processors <b>
 * must </b> synchronize shared fields like Collections by themselves. Multiple
 * constructors exist for different approaches creating this. You can create
 * this processor with either a Iterable of processors or a Consumer.
 *
 * For creating and managing threads a {@link Executors#newFixedThreadPool()} is
 * used. Creating more threads then cores can harm the performance. Using a
 * different thread pool could increase the performance, but this class should
 * be general usage. If you need better performance you may want to use an own
 * class with different parallel approach.
 */
public abstract class AbstractParallelProcessor<E extends CtElement> extends AbstractProcessor<E> {

	private ExecutorService service;
	private ArrayBlockingQueue<Processor<E>> processorQueue;

	/**
	 * Creates a new AbstractParallelProcessor from given iterable. The iterable is
	 * fully consumed. Giving an endless iterable of processors will result in
	 * errors. The processors must follow the guidelines given in the class
	 * description.
	 *
	 * @param processors iterable of processors.
	 * @throws IllegalArgumentException if size of iterable is less than 1.
	 *
	 */
	public AbstractParallelProcessor(Iterable<Processor<E>> processors) {
		// added cast because constructors need int
		int processorNumber = (int) StreamSupport.stream(processors.spliterator(), false).count();
		processorQueue = new ArrayBlockingQueue<>(processorNumber);
		processors.forEach(processorQueue::add);
		service = Executors.newFixedThreadPool(processorNumber);
	}

	/**
	 * Creates a new AbstractParallelProcessor from given iterable. The processors
	 * must follow the guidelines given in the class description.
	 *
	 * @param processors         iterable of processors.
	 * @param numberOfProcessors number consumed from the iterable added to the
	 *                           active processors.
	 * @throws SpoonException           if iterable has less values then
	 *                                  numberOfProcessors.
	 * @throws IllegalArgumentException if numberOfProcessors is less than 1.
	 *
	 */
	public AbstractParallelProcessor(Iterable<Processor<E>> processors, int numberOfProcessors) {
		processorQueue = new ArrayBlockingQueue<>(numberOfProcessors);
		service = Executors.newFixedThreadPool(numberOfProcessors);
		Iterator<Processor<E>> it = processors.iterator();
		for (int i = 0; i < numberOfProcessors; i++) {
			if (!it.hasNext()) {
				throw new SpoonException("not enough elements provided, iterable is already empty");
			}
			processorQueue.add(it.next());
		}
	}

	/**
	 * Creates a new AbstractParallelProcessor from given consumer. The processors
	 * must follow the guidelines given in the class description.
	 *
	 * @param processFunction    Represents an operation that accepts a single
	 *                           element E and returns no result.
	 * @param numberOfProcessors number of concurrent running processors.
	 * @throws IllegalArgumentException if numberOfProcessors is less than 1.
	 */
	public AbstractParallelProcessor(Consumer<E> processFunction, int numberOfProcessors) {
		processorQueue = new ArrayBlockingQueue<>(numberOfProcessors);
		for (int i = 0; i < numberOfProcessors; i++) {
			processorQueue.add(new AbstractProcessor<E>() {
				@Override
				public void process(E element) {
					processFunction.accept(element);
				}
			});
		}
		service = Executors.newFixedThreadPool(numberOfProcessors);
	}

	@Override
	public final void process(E element) {
		try {
			Processor<E> currentProcessor = processorQueue.take();
			service.execute(() -> {
				try {
					currentProcessor.process(element);
					processorQueue.put(currentProcessor);
				} catch (InterruptedException e) {
					// because rethrow is not possible here.
					Thread.currentThread().interrupt();
					e.printStackTrace();
					processorQueue.add(currentProcessor);
				} catch (Exception e) {
					// allows throwing exception, but keeping the processor in the queue
					processorQueue.add(currentProcessor);
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
	 * Cleans the threadpool after processing.
	 */
	@Override
	public void processingDone() {
		service.shutdown();
		super.processingDone();
	}
}
