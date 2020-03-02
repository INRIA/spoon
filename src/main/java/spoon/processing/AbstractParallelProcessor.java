/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

/**
 * AbstractParallelProcessor
 */
public abstract class AbstractParallelProcessor<E extends CtElement> extends AbstractProcessor<E> {

	private ExecutorService service;
	private ArrayBlockingQueue<Processor<E>> processorQueue;

	public AbstractParallelProcessor(Iterable<Processor<E>> processors) {
		// added casts because long number of processors seems big and constructors need
		// it
		int processorNumber = (int) StreamSupport.stream(processors.spliterator(), false).count();
		processorQueue = new ArrayBlockingQueue<>(processorNumber);
		processors.forEach(processorQueue::add);
		service = Executors.newFixedThreadPool(processorNumber);
	}

	public AbstractParallelProcessor(AbstractProcessorFactory<E> processorFactory, int numberOfProcessors) {
		processorQueue = new ArrayBlockingQueue<>(numberOfProcessors);
		service = Executors.newFixedThreadPool(numberOfProcessors);
		for (int i = 0; i < numberOfProcessors; i++) {
			processorQueue.add(processorFactory.createProcessor());
		}
	}

	/**
	 *
	 * @param processFunction
	 * @param numberOfProcessors
	 * @throws IllegalArgumentException if numberOfProcessors is less than 1
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

	@Override
	public void processingDone() {
		service.shutdown();
		super.processingDone();
	}
}
