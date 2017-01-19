/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor.chain;

/**
 * The evaluation context of the CtQuery. Can be used to bind the query the the output {@link CtConsumer}
 * using {@link CtQueryContext#setOutputConsumer(CtConsumer)} and then
 * <ul>
 * <li>to evaluate the query on provided input using {@link CtQueryContext#accept(Object)}
 * <li>to terminate the query evaluation at any phase of query execution using {@link CtQueryContext#terminate()}
 * <li>to check if query is terminated at any phase of query execution using {@link CtQueryContext#isTerminated()}
 * and to stop an expensive query evaluating process
 * </ul>
 */
public interface CtQueryContext extends CtConsumer<Object> {
	/**
	 * @return the {@link CtConsumer} used to deliver results of the query evaluation
	 */
	CtConsumer<?> getOutputConsumer();
	/**
	 * @param outputConsumer the {@link CtConsumer} used to deliver results of the query evaluation
	 * @return this to support fluent API
	 */
	CtQueryContext setOutputConsumer(CtConsumer<?> outputConsumer);

	/**
	 * terminates current query evaluation.
	 * This method returns normally. It does not throw exception.
	 * But it causes that query evaluation engine terminates
	 * and returns all the till now collected results.
	 */
	void terminate();
	/**
	 * @return true if evaluation has to be/was terminated
	 */
	boolean isTerminated();
}
