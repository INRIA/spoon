package spoon.support.visitor.java;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VariableScanner extends CtInheritanceScanner {
    private CtElement expression;
    final List<CtVariable> variables = new ArrayList<>();

    public VariableScanner(CtElement expression) {
        super();
        this.expression = expression;
    }

    @Override
    public void visitCtStatementList(CtStatementList e) {
        for (int i = 0; i < e.getStatements().size(); i++) {
            CtStatement ctStatement = e.getStatements().get(i);
            if (ctStatement.getPosition() == null) {
            }
            if (ctStatement.getPosition() != null
                    && ctStatement.getPosition().getSourceStart() > expression.getPosition().getSourceEnd()) {
                break;
            }
            if (ctStatement instanceof CtVariable) {
                variables.add((CtVariable) ctStatement);
            }
        }
        super.visitCtStatementList(e);
    }

    @Override
    public <T> void scanCtType(CtType<T> type) {
        List<CtField<?>> fields = type.getFields();
        for (int i = 0; i < fields.size(); i++) {
            CtField<?> ctField = fields.get(i);
            if (ctField.hasModifier(ModifierKind.PUBLIC) || ctField.hasModifier(ModifierKind.PROTECTED)) {
                variables.add(ctField);
            } else if (ctField.hasModifier(ModifierKind.PRIVATE)) {
                if (expression.hasParent(type)) {
                    variables.add(ctField);
                }
            } else if (expression.getParent(CtPackage.class).equals(type.getParent(CtPackage.class))) {
                // default visibility
                variables.add(ctField);
            }
        }

        CtTypeReference<?> superclass = type.getSuperclass();
        if (superclass != null) {
            this.scan(superclass.getTypeDeclaration());
        }
        Set<CtTypeReference<?>> superInterfaces = type.getSuperInterfaces();
        for (Iterator<CtTypeReference<?>> iterator = superInterfaces.iterator(); iterator.hasNext();) {
            CtTypeReference<?> typeReference = iterator.next();
            this.scan(typeReference.getTypeDeclaration());
        }
    }

    @Override
    public void visitCtTryWithResource(CtTryWithResource e) {
        variables.addAll(e.getResources());
    }

    @Override
    public void scanCtExecutable(CtExecutable e) {
        variables.addAll(e.getParameters());
    }

    @Override
    public void visitCtFor(CtFor e) {
        for (CtStatement ctStatement : e.getForInit()) {
            this.scan(ctStatement);
        }
    }

    @Override
    public void visitCtForEach(CtForEach e) {
        variables.add(e.getVariable());
    }
}