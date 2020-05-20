package spoon.kotlin.reflect

import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtStatement
import spoon.reflect.declaration.CtTypedElement
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.visitor.CtVisitor
import spoon.support.reflect.code.CtStatementImpl

interface KtExpressionStatement<T> : CtStatement, CtExpression<T> {
    override fun clone() : KtExpressionStatement<T>
    var expression : CtExpression<T>
}

class KtExpressionStatementImpl<T>(override var expression : CtExpression<T>) :
        CtStatementImpl(), KtExpressionStatement<T>
{

    override fun clone(): KtExpressionStatement<T> {
        return KtExpressionStatementImpl<T>(expression.clone())
    }

    override fun accept(visitor: CtVisitor) {
        expression.accept(visitor)
    }

    override fun <C : CtTypedElement<*>?> setType(type: CtTypeReference<T>?): C = expression.setType<C>(type)

    override fun S(): T? = expression.S()

    override fun getTypeCasts(): MutableList<CtTypeReference<*>> = expression.typeCasts

    override fun <C : CtExpression<T>?> addTypeCast(p0: CtTypeReference<*>?): C = expression.addTypeCast<C>(p0)

    override fun <C : CtExpression<T>?> setTypeCasts(casts: MutableList<CtTypeReference<*>>?): C = expression.setTypeCasts<C>(casts)

    override fun getType(): CtTypeReference<T> = expression.type



}



