package spoon.reflect.visitor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * ParallelEarlyTerminatingScanner
 */
public class ParallelEarlyTerminatingScanner<T> extends EarlyTerminatingScanner<T> {

	private ExecutorService service;
	private ArrayBlockingQueue<EarlyTerminatingScanner<T>> scannerQueue;
	private AtomicBoolean isTerminated = new AtomicBoolean(false);

	public ParallelEarlyTerminatingScanner(Iterable<EarlyTerminatingScanner<T>> scanners) {
		// added cast because constructors need int
		int scannerNumber = (int) StreamSupport.stream(scanners.spliterator(), false).count();
		scannerQueue = new ArrayBlockingQueue<>(scannerNumber);
		scanners.forEach(scannerQueue::add);
		service = Executors.newFixedThreadPool(scannerNumber);
	}

	public ParallelEarlyTerminatingScanner(Iterable<EarlyTerminatingScanner<T>> scanners, int numberOfScanners) {
		scannerQueue = new ArrayBlockingQueue<>(numberOfScanners);
		service = Executors.newFixedThreadPool(numberOfScanners);
		Iterator<EarlyTerminatingScanner<T>> it = scanners.iterator();
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
					// allows throwing exception, but keeping the processor in the queue
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

	// write in Doc that this method cant be deterministic.
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
}
