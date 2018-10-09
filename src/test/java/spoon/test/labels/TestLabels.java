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
package spoon.test.labels;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NamedElementFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 19/06/2017.
 */
public class TestLabels {
    @Test
    public void testLabelsAreDetected() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/labels/testclasses/ManyLabels.java");
        launcher.buildModel();

        CtMethod mainMethod = launcher.getFactory().getModel().getElements(new NamedElementFilter<>(CtMethod.class,"main")).get(0);

        CtBlock body = mainMethod.getBody();
        assertEquals(2, body.getStatements().size());

        CtBlock block = (CtBlock) body.getStatement(0);
        CtSwitch ctSwitch = (CtSwitch) body.getStatement(1);

        assertEquals("labelBlock",block.getLabel());
        assertEquals("sw", ctSwitch.getLabel());

        assertTrue(block.getStatement(1) instanceof CtIf);

        CtIf firstIf = (CtIf) block.getStatement(1);

        CtBlock then = firstIf.getThenStatement();
        CtBreak firstBreak = (CtBreak) then.getStatement(1);

        assertEquals("labelBlock", firstBreak.getTargetLabel());
        assertSame(block, firstBreak.getLabelledStatement());

        CtIf secondIf = (CtIf) block.getStatement(2);
        assertEquals("labelIf", secondIf.getLabel());

        CtBlock thenBlock = secondIf.getThenStatement();
        CtIf innerIf = (CtIf) thenBlock.getStatement(0);

        CtBlock innerThenBlock = innerIf.getThenStatement();
        CtBreak breakInnerIf = (CtBreak) innerThenBlock.getStatement(0);
        assertSame(secondIf, breakInnerIf.getLabelledStatement());

        CtCase firstCase = (CtCase) ctSwitch.getCases().get(0);
        List<CtStatement> statementList = firstCase.getStatements();

        assertEquals(2, statementList.size());

        CtDo ctDo = (CtDo) statementList.get(0);
        assertEquals("label", ctDo.getLabel());

        CtBreak finalBreak = (CtBreak) statementList.get(1);
        assertNull(finalBreak.getTargetLabel());
        assertNull(finalBreak.getLabelledStatement());

        CtBlock doBlock = (CtBlock) ctDo.getBody();
        CtWhile ctWhile = (CtWhile) doBlock.getStatement(1);
        assertEquals("lWhile", ctWhile.getLabel());

        CtBlock whileBlock = (CtBlock) ctWhile.getBody();
        CtFor forLoop = (CtFor) whileBlock.getStatement(0);
        CtBreak breakSwitch = (CtBreak) whileBlock.getStatement(1);

        assertEquals("sw", breakSwitch.getTargetLabel());
        assertSame(ctSwitch, breakSwitch.getLabelledStatement());

        assertEquals("forloop", forLoop.getLabel());
        CtBlock forBlock = (CtBlock) forLoop.getBody();

        assertEquals(7, forBlock.getStatements().size());
        CtIf firstForIf = (CtIf) forBlock.getStatement(1);
        CtIf secondForIf = (CtIf) forBlock.getStatement(2);
        CtIf thirdForIf = (CtIf) forBlock.getStatement(3);
        CtIf fourthForIf = (CtIf) forBlock.getStatement(4);

        CtBreak breakItself = (CtBreak) forBlock.getStatement(6);

        CtContinue continueFor = (CtContinue) ((CtBlock) firstForIf.getThenStatement()).getStatement(0);
        assertSame(forLoop, continueFor.getLabelledStatement());

        CtContinue continueWhile = (CtContinue) ((CtBlock) secondForIf.getThenStatement()).getStatement(0);
        assertSame(ctWhile, continueWhile.getLabelledStatement());

        CtContinue continueDo = (CtContinue) ((CtBlock) thirdForIf.getThenStatement()).getStatement(0);
        assertSame(ctDo, continueDo.getLabelledStatement());

        CtBreak breakDo = (CtBreak) ((CtBlock) fourthForIf.getThenStatement()).getStatement(0);
        assertSame(ctDo, breakDo.getLabelledStatement());

        assertEquals("labelbreak", breakItself.getLabel());
        assertEquals("labelbreak", breakItself.getTargetLabel());
        assertSame(breakItself, breakItself.getLabelledStatement());
    }
}
