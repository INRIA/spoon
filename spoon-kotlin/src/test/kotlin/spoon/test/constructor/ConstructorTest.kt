package spoon.test.constructor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.reflect.declaration.CtAnonymousExecutable
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtConstructor
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.constructor.testclasses.*

class ConstructorTest {
    val util = TestBuildUtil
    val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    private fun CtConstructor<*>.isPrimary() = this.getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as Boolean
    private fun CtConstructor<*>.getDelegate() =
        this.body.statements.getOrNull(0) as? CtConstructorCall<*>?

    @Test
    fun testImplicitConstructor() {
        // Contract
        // WHEN: Class is declared without explicit primary constructor, but no secondary constructors
        // THEN: Primary constructor is implicitly added
        val c = util.buildClass("spoon.test.constructor.testclasses","ImplicitConstructor") as CtClass<*>
        assertTrue(c.getConstructor().isImplicit)
        assertEquals("()", pp.prettyprint(c.getConstructor()))
        assertTrue(c.getConstructor().isPrimary())
    }

    @Test
    fun testExplicitConstructor() {
        // Contract
        // WHEN: Class is declared with explicit primary constructor without parameters, but no secondary constructors
        // THEN: Primary constructor is explicit
        val c = util.buildClass("spoon.test.constructor.testclasses","ExplicitPrimaryConstructor") as CtClass<*>
        assertFalse(c.getConstructor().isImplicit)
        assertEquals("()", pp.prettyprint(c.getConstructor()))
        assertTrue(c.getConstructor().isPrimary())
    }

    @Test
    fun testPrimaryAndSecondaryConstructors() {
        // Contract
        // WHEN: Class has both primary and secondary constructors
        // THEN: Primary constructor is not implicit, only primary constructor is marked as primary, all secondary
        // constructors have a "this" delegate
        val c = util.buildClass("spoon.test.constructor.testclasses","PrimaryAndSecondaryConstructors") as CtClass<*>
        val constructors = c.constructors.toList()
        assertEquals(3, constructors.size)
        assertTrue(constructors.all { !it.isImplicit })
        assertTrue(constructors[0].isPrimary())
        assertFalse(constructors[1].isPrimary())
        assertFalse(constructors[2].isPrimary())

        assertEquals("()", pp.prettyprint(constructors[0]))
        assertEquals("constructor(n: kotlin.Int) : this()", pp.prettyprint(constructors[1]))
        assertEquals("constructor(k: kotlin.String) : this(1)", pp.prettyprint(constructors[2]))

    }

    @Test
    fun testSimpleSuperDelegation() {
        // Contract
        // WHEN: Class extends superclass, primary constructor is NOT explicitly declared, secondary constructors are declared
        // THEN: No constructor is primary or implicit. All constructors must have delegate call
        val c = util.buildClass("spoon.test.constructor.testclasses","SimpleSuperDelegation") as CtClass<*>
        val constructors = c.constructors.toList()
        assertEquals(2, constructors.size)
        assertTrue(constructors.all { !it.isImplicit })
        assertTrue(constructors.all { !it.isPrimary() })

        assertEquals("constructor() : super()", pp.prettyprint(constructors[0]))
        assertEquals("constructor(s: kotlin.String) : super()", pp.prettyprint(constructors[1]))
        assertEquals("SimpleSuperDelegationBase", c.superclass.simpleName)

        assertEquals(c.factory.Type().createReference(SimpleSuperDelegationBase::class.java), constructors[0].getDelegate()!!.type)
        assertEquals(c.factory.Type().createReference(SimpleSuperDelegation::class.java), constructors[0].type)
        assertEquals(c.factory.Type().createReference(SimpleSuperDelegationBase::class.java), constructors[1].getDelegate()!!.type)
        assertEquals(c.factory.Type().createReference(SimpleSuperDelegation::class.java), constructors[1].type)
    }

    @Test
    fun testMixedThisAndSuperDelegation() {
        // Contract
        // WHEN: Class extends superclass without primary constructor, primary constructor is NOT explicitly declared,
        // secondary constructors are declared
        // THEN: No constructor is primary or implicit. All constructors must have delegate call to this or super.
        // Those delegates have type of class if it is "this", and type of superclass if it is "super".
        val c = util.buildClass("spoon.test.constructor.testclasses","MixedThisAndSuperDelegation") as CtClass<*>
        val constructors = c.typeMembers.map { it as CtConstructor<*> }
        assertEquals(5, constructors.size)
        assertTrue(constructors.all { !it.isImplicit })
        assertTrue(constructors.all { !it.isPrimary() })
        assertTrue(constructors.all { c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java) == it.type })

        assertEquals("constructor() : super()", pp.prettyprint(constructors[0]))
        assertEquals("constructor(s: kotlin.String) : super(s)", pp.prettyprint(constructors[1]))
        assertEquals("constructor(n: kotlin.Int) : this(n.toString())", pp.prettyprint(constructors[2]))
        assertEquals("constructor(n: kotlin.Byte) : this()", pp.prettyprint(constructors[3]))
        assertEquals("constructor(n: kotlin.Short) : super()", pp.prettyprint(constructors[4]))

        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegationBase::class.java), constructors[0].getDelegate()?.type)
        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[0].type)

        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegationBase::class.java), constructors[1].getDelegate()?.type)
        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[1].type)

        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[2].getDelegate()?.type)
        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[2].type)

        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[3].getDelegate()?.type)
        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[3].type)

        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegationBase::class.java), constructors[4].getDelegate()?.type)
        assertEquals(c.factory.Type().createReference(MixedThisAndSuperDelegation::class.java), constructors[4].type)

    }

    @Test
    fun testPropertyDeclaringPrimaryConstructor() {
        // Contract
        // WHEN: Properties are declared in primary constructor
        // THEN: Parameters have the same modifiers, implicit fields are created with the same name
        val c = util.buildClass("spoon.test.constructor.testclasses","PropertyDeclaringPrimaryConstructor") as CtClass<*>
        val constructors = c.constructors.toList()
        assertEquals(1, constructors.size)
        val constructor = constructors[0]

        assertEquals(4, c.allFields.size)
        assertFalse(constructor.isImplicit)
        assertTrue(constructor.isPrimary())
        assertEquals(c.factory.Type().createReference(PropertyDeclaringPrimaryConstructor::class.java), constructor.type)

        val p1 = c.getField("p1")
        val p2 = c.getField("p2")
        val p3 = c.getField("p3")
        val p4 = c.getField("p4")
        assertTrue(listOf(p1,p2,p3,p4).all { it.isImplicit })

        assertEquals("private var p1: kotlin.Int", pp.prettyprint(constructor.parameters[0]))
        assertEquals("internal val p2: kotlin.String", pp.prettyprint(constructor.parameters[1]))
        assertEquals("open val p3: kotlin.Byte", pp.prettyprint(constructor.parameters[2]))
        assertEquals("protected var p4: kotlin.Double", pp.prettyprint(constructor.parameters[3]))

        // Look at FQ name. because createReference(Int::class.java) will point to primitive JVM int, not kotlin.Int
        assertEquals("kotlin.Int", constructor.parameters[0].type.qualifiedName)
        assertEquals("kotlin.Int", p1.type.qualifiedName)
        assertEquals("kotlin.String", constructor.parameters[1].type.qualifiedName)
        assertEquals("kotlin.String", p2.type.qualifiedName)
        assertEquals("kotlin.Byte", constructor.parameters[2].type.qualifiedName)
        assertEquals("kotlin.Byte", p3.type.qualifiedName)
        assertEquals("kotlin.Double", constructor.parameters[3].type.qualifiedName)
        assertEquals("kotlin.Double", p4.type.qualifiedName)
    }

    @Test
    fun testAnonymousInitBlock() {
        // Contracts:
        // Init blocks are built in order
        // Init block has access to primary constructor parameters
        val c = util.buildClass("spoon.test.constructor.testclasses","AnonymousInit") as CtClass<*>

        val inits = c.getElements(TypeFilter(CtAnonymousExecutable::class.java))
        assertEquals(3, inits.size)
        var initBlock = inits[0]
        assertEquals(2, initBlock.body.statements.size)
        var localVar = initBlock.body.statements[0] as CtLocalVariable<*>
        assertEquals("val i1 = \"init1\"", pp.prettyprint(localVar))

        var assignment = initBlock.body.statements[1] as CtAssignment<*,*>
        assertEquals("p = param", pp.prettyprint(assignment))
        assertTrue(assignment.assignment is CtVariableRead<*>)
        assertTrue(assignment.assigned is CtFieldWrite<*>)

        val param = (assignment.assignment as CtVariableRead<*>).variable
        assertEquals(param, c.constructors.toList()[0].parameters[0].reference)

        initBlock = inits[1]
        localVar = initBlock.body.statements[0] as CtLocalVariable<*>
        assertEquals("val i2 = \"init2\"", pp.prettyprint(localVar))

        assignment = initBlock.body.statements[1] as CtAssignment<*,*>
        assertTrue(assignment.assignment is CtLiteral<*>)
        assertTrue(assignment.assigned is CtFieldWrite<*>)

        assertEquals("init {}", pp.prettyprint(inits[2]))
    }
}