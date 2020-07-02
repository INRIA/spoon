package spoon.test.generics

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.CtInvocation
import spoon.reflect.code.CtLocalVariable
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtMethod
import spoon.reflect.reference.CtTypeParameterReference
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.reference.CtWildcardReference
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

class GenericsTest {

    @Test
    fun testBuildSimpleTypeParams() {
        val c = TestBuildUtil.buildClass("spoon.test.generics.testclasses","SimpleTypeParam")

        assertEquals(1, c.formalCtTypeParameters.size)
        val typeParam = c.formalCtTypeParameters[0]
        assertNull(typeParam.superclass)

        val f1 = c.getField("t1")
        val f2 = c.getField("t2")

        var type = f1.type
        checkTypeParamRef(type, "T", typeParam, f1)
        assertFalse(type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as Boolean)
        assertEquals("T", type.asString())

        type = f2.type
        checkTypeParamRef(type, "T", typeParam, f2)
        assertTrue(type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as Boolean)
        assertEquals("T?", type.asString())

        assertEquals("val t1: T = ct", f1.asString())
        assertEquals("val t2: T? = ct", f2.asString())

        val constr = (c as CtClass<*>).constructors.toList()[0]
        assertEquals(0, constr.formalCtTypeParameters.size)
        assertEquals(1, constr.parameters.size)
        val constrParam = constr.parameters[0]

        type = constrParam.type
        checkTypeParamRef(type, "T", typeParam, constrParam)
        assertFalse(type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as Boolean)
        assertEquals("T", type.asString())

        val method = c.getMethodByName("m")
        type = method.type
        checkTypeParamRef(type, "T", typeParam, method)
        assertFalse(type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as Boolean)
        assertEquals("T", type.asString())

        assertEquals(1, method.formalCtTypeParameters.size)
        assertEquals(1, method.parameters.size)
        val methodTypeParam = method.formalCtTypeParameters[0]
        val valueParam = method.parameters[0]
        assertNull(methodTypeParam.superclass)
        assertEquals("S", methodTypeParam.simpleName)

        type = valueParam.type
        checkTypeParamRef(type, "S", methodTypeParam, valueParam)
        assertFalse(type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as Boolean)
        assertEquals("S", type.asString())

        method.setBody<CtMethod<*>>(null)
        assertEquals("fun <S> m(s: S): T", method.asString())
    }

    private fun checkTypeParamRef(ref: CtTypeReference<*>, name: String, pointsTo: CtElement, parent: CtElement) {
        assertEquals(name, ref.simpleName)
        assertTrue(ref is CtTypeParameterReference)
        assertNull(ref.`package`)
        assertNull(ref.declaringType)
        assertEquals(0, ref.actualTypeArguments.size)
        assertSame(pointsTo, ref.declaration)
        assertSame(parent, ref.parent)
    }

    @Test
    fun testTypeParamModifiers() {
        val c = TestBuildUtil.buildClass("spoon.test.generics.testclasses","Variances")
        assertEquals(2, c.formalCtTypeParameters.size)
        val t = c.formalCtTypeParameters[0]
        val s = c.formalCtTypeParameters[1]

        val m = c.getMethodByName("reified")
        assertEquals(1, m.formalCtTypeParameters.size)
        val r = m.formalCtTypeParameters[0]

        assertEquals(setOf(KtModifierKind.TYPE_PROJECTION_IN), t.getMetadata(KtMetadataKeys.KT_MODIFIERS))
        assertEquals("in T", t.asString())

        assertEquals(setOf(KtModifierKind.TYPE_PROJECTION_OUT), s.getMetadata(KtMetadataKeys.KT_MODIFIERS))
        assertEquals("out S", s.asString())

        assertEquals(setOf(KtModifierKind.REIFIED), r.getMetadata(KtMetadataKeys.KT_MODIFIERS))
        assertEquals("reified R", r.asString())

        m.setBody<CtMethod<*>>(null)
        assertEquals("inline fun <reified R> reified(r: R)", m.asString())

        val starM = c.getMethodByName("star")
        assertEquals(1, starM.parameters.size)
        val arrayType = starM.parameters[0].type
        assertEquals(1, arrayType.actualTypeArguments.size)
        val star = arrayType.actualTypeArguments[0]
        assertTrue(star is CtWildcardReference)
        assertEquals("*", star.asString())
        starM.setBody<CtMethod<*>>(null)
        assertEquals("fun star(a: kotlin.Array<*>)", starM.asString())
    }

    @Test
    fun testNestedTypeArg() {
        val c = TestBuildUtil.buildClass("spoon.test.generics.testclasses","SimpleTypeParam")

        val initializer = c.getField("nestedTypeArg").defaultExpression as CtInvocation<*> // emptyList<Pair<*,Comparable<T>>>
        assertEquals(1, initializer.actualTypeArguments.size)

        val pair = initializer.actualTypeArguments[0] // Pair<*,Comparable<T>>
        assertEquals(2, pair.actualTypeArguments.size)

        val star = pair.actualTypeArguments[0] // *
        assertEquals(0, star.actualTypeArguments.size)

        val comparable = pair.actualTypeArguments[1] // Comparable<T>
        assertEquals(1, comparable.actualTypeArguments.size)

        val t = comparable.actualTypeArguments[0] // T
        assertEquals(0, t.actualTypeArguments.size)
        assertEquals("T", t.qualifiedName)
        assertSame(c.formalCtTypeParameters[0], t.declaration)
        assertEquals("T", t.asString())

        assertTrue(star is CtWildcardReference)

        assertEquals("kotlin.Comparable", comparable.qualifiedName)
        assertEquals("kotlin.Comparable<T>", comparable.asString())

        assertEquals("kotlin.Pair", pair.qualifiedName)
        assertEquals("kotlin.Pair<*, kotlin.Comparable<T>>", pair.asString())

        assertEquals("emptyList<kotlin.Pair<*, kotlin.Comparable<T>>>()", initializer.asString())
    }

    @Test
    fun testActualTypeContainers() {
        /* Contract: Subclasses of CtActualTypeContainer shall be able to take type arguments
            CtExecutableReference
            CtIntersectionTypeReference TODO
            CtInvocation
            CtNewClass TODO
            CtTypeReference
            N/A:
            CtArrayType - No array types in Kotlin
            CtTypeParameterReference - Illegal in Kotlin
            CtConstructorCall - Regular invocation in Kotlin
            CtWildcardReference - Used as star projection in Kotlin
         */
        val c = TestBuildUtil.buildClass("spoon.test.generics.testclasses", "TypeArguments")
        val m = c.getMethodByName("m")
        assertEquals(1, m.formalCtTypeParameters.size)
        assertEquals(3, m.body.statements.size)
        val t = m.formalCtTypeParameters[0]

        var invocation = (m.body.statements[0] as CtLocalVariable<*>).assignment as CtInvocation<*>
        assertEquals(1, invocation.actualTypeArguments.size)
        assertSame(t, invocation.actualTypeArguments[0].declaration)
        assertEquals("ArrayList<T>()", invocation.asString())

        invocation = (m.body.statements[1] as CtLocalVariable<*>).assignment as CtInvocation<*>
        assertEquals(1, invocation.actualTypeArguments.size)
        assertEquals("kotlin.String", invocation.actualTypeArguments[0].qualifiedName)
        assertEquals("emptyList<kotlin.String>()", invocation.asString())

        val variable = (m.body.statements[2] as CtLocalVariable<*>)
        assertEquals(1, variable.type.actualTypeArguments.size)
        assertEquals("kotlin.Boolean", variable.type.actualTypeArguments[0].qualifiedName)
        assertEquals("kotlin.collections.ArrayList<kotlin.Boolean>", variable.type.asString())
    }

    @Test
    fun testImplicitTypeArguments() {
        val c = TestBuildUtil.buildClass("spoon.test.generics.testclasses", "TypeArguments")
        val implicit = c.getField("implicit").defaultExpression as CtInvocation<*>
        val explicit = c.getField("explicit").defaultExpression as CtInvocation<*>
        assertEquals(1, implicit.actualTypeArguments.size)
        assertEquals(1, explicit.actualTypeArguments.size)

        assertTrue(implicit.actualTypeArguments[0].isImplicit)
        assertFalse(explicit.actualTypeArguments[0].isImplicit)

        assertEquals("kotlin.Int", implicit.actualTypeArguments[0].qualifiedName)
        assertEquals("listOf(1, 2, 3)", implicit.asString())

        assertEquals("kotlin.Int", explicit.actualTypeArguments[0].qualifiedName)
        assertEquals("listOf<kotlin.Int>(4, 5, 6)", explicit.asString())
    }
}