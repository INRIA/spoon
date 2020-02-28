/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import spoon.reflect.declaration.CtElement;

/**
 * AbstractParallelProcessor
 */
public abstract class AbstractParallelProcessor<E extends CtElement> extends AbstractProcessor<E> {

	private ExecutorService service;
	private ArrayBlockingQueue<Processor<E>> processorQueue;

	public AbstractParallelProcessor(Collection<Processor<E>> processors) {
		processorQueue = new ArrayBlockingQueue<>(processors.size());
		processorQueue.addAll(processors);
		service = Executors.newFixedThreadPool(processors.size());
	}

	@Override
	public final void process(E element) {
		try {
			Processor<E> usedProcessor = processorQueue.take();
			service.execute(() -> {
				try {
					usedProcessor.process(element);
					processorQueue.add(usedProcessor);
				} catch (Exception e) {
					// allows throwing exception, but keeping the processor in the queue
					processorQueue.add(usedProcessor);
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
