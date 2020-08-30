package spoon.kotlin.reflect

import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtStatement
import spoon.reflect.code.CtStatementList
import spoon.reflect.declaration.CtElement
import spoon.reflect.visitor.CtVisitor
import spoon.support.reflect.code.CtExpressionImpl
import spoon.support.reflect.code.CtStatementImpl

interface KtStatementExpression<T> : CtExpression<T>, CtStatement {
    override fun clone(): KtStatementExpression<T>
    var statement : CtStatement
}

class KtStatementExpressionImpl<T>(override var statement: CtStatement) : CtExpressionImpl<T>(), KtStatementExpression<T> {

    override fun accept(visitor: CtVisitor?) { statement.accept(visitor) }

    override fun clone(): KtStatementExpression<T> = KtStatementExpressionImpl(statement.clone())

    override fun <T : CtStatement?> setLabel(label: String?): T = statement.setLabel<T>(label)

    override fun <T : CtStatement?> insertBefore(other: CtStatement?): T {
        CtStatementImpl.insertBefore(this, other)
        return this as T
    }

    override fun <T : CtStatement?> insertBefore(others: CtStatementList?): T {
        CtStatementImpl.insertBefore(this, others)
        return this as T
    }

    override fun <T : CtStatement?> insertAfter(other: CtStatement?): T {
        CtStatementImpl.insertAfter(this, other)
        return this as T
    }

    override fun <T : CtStatement?> insertAfter(others: CtStatementList?): T {
        CtStatementImpl.insertAfter(this, others)
        return this as T
    }

    override fun getLabel(): String = statement.label

    override fun getMetadata(key: String): Any? {
        return statement.getMetadata(key)
    }

    override fun <E : CtElement?> putMetadata(key: String, `val`: Any?): E {
        return statement.putMetadata<E>(key, `val`)
    }

    override fun isImplicit(): Boolean {
        return statement.isImplicit
    }
}