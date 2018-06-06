/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * PropertyComparator performs a comparison of two beans,
 * evaluating the specified bean property via a BeanWrapper.
 *
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 19.05.2003
 * @see org.springframework.beans.BeanWrapper
 */
public class PropertyComparator<T> implements Comparator<T> {

	@Override
	@SuppressWarnings("unchecked")
	public int compare(T o1, T o2) {
		return o1.toString().compareTo(o2.toString());
	}
}
