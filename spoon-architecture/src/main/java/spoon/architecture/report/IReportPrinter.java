package spoon.architecture.report;

import java.lang.reflect.Method;

public interface IReportPrinter {

	default void startPrinting() {

	}

	default void beforeMethod(Method method) {

	}

	default void afterMethod(Method method) {

	}

	default void finishPrinting() {

	}
}
