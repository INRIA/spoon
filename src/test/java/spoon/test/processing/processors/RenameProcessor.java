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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

public class RenameProcessor extends AbstractProcessor<CtElement> {
    private String oldName;
    private String newName;

    public RenameProcessor(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        if (candidate instanceof CtNamedElement) {
            CtNamedElement namedElement = (CtNamedElement) candidate;
            return namedElement.getSimpleName().equals(oldName);
        } else if (candidate instanceof CtReference) {
            CtReference reference = (CtReference) candidate;
            return reference.getSimpleName().equals(oldName);
        }
        return false;
    }

    @Override
    public void process(CtElement element) {
        if (element instanceof CtNamedElement) {
            CtNamedElement namedElement = (CtNamedElement) element;
            namedElement.setSimpleName(this.newName);
        } else if (element instanceof CtReference) {
            CtReference reference = (CtReference) element;
            reference.setSimpleName(this.newName);
        }
    }
}
