/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.test.processing.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urli on 10/08/2017.
 */
public abstract class GenericCtTypeProcessor<T extends CtType> extends AbstractProcessor<T> {

    public GenericCtTypeProcessor(Class<T> zeClass) {
        super.addProcessedElementType(zeClass);
    }

    public List<T> elements = new ArrayList<>();

    @Override
    public void process(T element) {
        elements.add(element);
    }
}
