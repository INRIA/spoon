package fr.inria.spoon.dataflow.warning;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

public class Warning
{
    public CtElement element;
    public WarningKind kind;
    public SourcePosition position;
    public String message;

    public Warning(CtElement element, WarningKind kind)
    {
        this.element = element;
        this.kind = kind;
        this.position = element.getPosition();
        this.message = String.format(kind.message, element.toString());
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", message, position.toString());
    }
}
