package spoon.test.field

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtFieldRead
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.getKtModifiers

class FieldTest {
    private val util = TestBuildUtil
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    private fun CtField<*>.getDelegate() = getMetadata(KtMetadataKeys.PROPERTY_DELEGATE) as CtExpression<*>?

    @Test
    fun testBuildPropertiesWithoutDelegates() {
        val c = util.buildClass("spoon.test.field.testclasses","Properties")

        assertEquals(6, c.fields.size)
        assertEquals(6, c.declaredFields.size)

        var property = c.getField("f0")
        assertSame(c.fields[0], property)
        assertEquals("kotlin.Int", property.type.qualifiedName)
        assertEquals("val f0 = 0", pp.prettyprint(property)) // Public should be ignored
        assertEquals(setOf(KtModifierKind.PUBLIC,KtModifierKind.FINAL,KtModifierKind.VAL), property.getKtModifiers())

        property = c.getField("f1")
        assertSame(c.fields[1], property)
        assertEquals("kotlin.Long", pp.prettyprint(property.type))
        assertEquals("protected var f1: kotlin.Long = 1L", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PROTECTED,KtModifierKind.FINAL,KtModifierKind.VAR), property.getKtModifiers())

        property = c.getField("f2")
        assertSame(c.fields[2], property)
        assertEquals("kotlin.String", property.type.qualifiedName)
        assertEquals("private val f2 = \"private property\"", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PRIVATE,KtModifierKind.FINAL,KtModifierKind.VAL), property.getKtModifiers())

        property = c.getField("f3")
        assertSame(c.fields[3], property)
        assertEquals("kotlin.Float", property.type.qualifiedName)
        assertEquals("internal open var f3 = 3.0F", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.INTERNAL,KtModifierKind.OPEN,KtModifierKind.VAR), property.getKtModifiers())

        property = c.getField("f4")
        assertSame(c.fields[4], property)
        assertEquals("kotlin.Double", pp.prettyprint(property.type))
        assertEquals("abstract val f4: kotlin.Double", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PUBLIC,KtModifierKind.ABSTRACT,KtModifierKind.VAL), property.getKtModifiers())

        property = c.getField("f5")
        assertSame(c.fields[5], property)
        assertEquals("kotlin.String", pp.prettyprint(property.type))
        assertEquals("lateinit var f5: kotlin.String", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PUBLIC,KtModifierKind.FINAL,KtModifierKind.LATEINIT,KtModifierKind.VAR), property.getKtModifiers())
    }

    @Test
    fun testBuildPropertyWithDelegate() {
        val c = util.buildClass("spoon.test.field.testclasses","DelegatedProperties")
        val lineSeparator = System.getProperty("line.separator")
        val indent = "    "
        assertEquals(2, c.fields.size)
        assertEquals(2, c.declaredFields.size)

        var property = c.getField("lazyInt")
        assertEquals("kotlin.Int", property.type.qualifiedName)
        assertEquals("val lazyInt by lazy { 123 }", pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PUBLIC,KtModifierKind.FINAL,KtModifierKind.VAL), property.getKtModifiers())

        property = c.getField("lazyString")
        assertEquals("kotlin.String", property.type.qualifiedName)
        assertEquals("val lazyString by lazy(fun(): kotlin.String {$lineSeparator$indent${indent}return \"s\"$lineSeparator$indent})",
            pp.prettyprint(property))
        assertEquals(setOf(KtModifierKind.PUBLIC,KtModifierKind.FINAL,KtModifierKind.VAL), property.getKtModifiers())
    }

    @Test
    fun testFieldImplicitTarget() {
        val c = util.buildClass("spoon.test.field.testclasses","DelegatedProperties")

        val fieldReads = c.getElements(TypeFilter(CtFieldRead::class.java))
        assertEquals(1, fieldReads.size)
        assertEquals("lazyInt", pp.prettyprint(fieldReads[0]))
        assertTrue(fieldReads[0].target.isImplicit)
        fieldReads[0].target.setImplicit<CtElement>(false)
        assertFalse(fieldReads[0].target.isImplicit)
        assertEquals("this.lazyInt", pp.prettyprint(fieldReads[0]))
    }
}