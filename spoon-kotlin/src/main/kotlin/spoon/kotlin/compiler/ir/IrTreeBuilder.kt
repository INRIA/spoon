package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import spoon.reflect.declaration.CtElement
import spoon.reflect.factory.Factory

class IrTreeBuilder(val factory: Factory): IrElementVisitor<CtElement, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?): CtElement {
        //TODO("Not yet implemented")
        return factory.Core().createContinue()
    }


}