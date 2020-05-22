package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.reference.*
import spoon.reflect.visitor.CtVisitor
import spoon.reflect.visitor.PrettyPrinter

class DefaultKotlinPrettyPrinter(
    private val adapter: DefaultPrinterAdapter,
    private val forceExplicitTypes : Boolean = false
    //     private val sourceCompilationUnit : CtCompilationUnit
) : CtVisitor, PrettyPrinter {

    private val LEFT_ROUND = '('
    private val RIGHT_ROUND = ')'
    private val LEFT_SQUARE = '['
    private val RIGHT_SQUARE = ']'
    private val LEFT_CURL = '{'
    private val RIGHT_CURL = '}'
    private val SPACE = ' '

    /**
     * Filter the Set using the predicate only if the condition holds, otherwise returns the Set.
     */
    private inline fun <T> Set<T>.filterIf(condition : Boolean, filterPred : (T) -> Boolean) : Set<T> =
        if(!condition) this else this.filter(filterPred).toSet()

    @Suppress("UNCHECKED_CAST")
    private fun getModifiersMetadata(e : CtElement) : Set<KtModifierKind>? =
        e.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>

    override fun getResult(): String {
        return adapter.toString()
    }

    override fun prettyprint(p0: CtElement?): String {
        TODO("Not yet implemented")
    }

    override fun printElement(p0: CtElement?): String {
        TODO("Not yet implemented")
    }

    override fun printTypes(vararg p0: CtType<*>?): String {
        TODO("Not yet implemented")
    }

    override fun calculate(p0: CtCompilationUnit?, p1: MutableList<CtType<*>>?) {
        TODO("Not yet implemented")
    }

    override fun printCompilationUnit(p0: CtCompilationUnit?): String {
        TODO("Not yet implemented")
    }

    override fun printModuleInfo(p0: CtModule?): String {
        TODO("Not yet implemented")
    }

    override fun printPackageInfo(p0: CtPackage?): String {
        TODO("Not yet implemented")
    }

    override fun getLineNumberMapping(): MutableMap<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtConditional(p0: CtConditional<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtIntersectionTypeReference(p0: CtIntersectionTypeReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtLambda(p0: CtLambda<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtForEach(p0: CtForEach?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtConstructorCall(p0: CtConstructorCall<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtUnaryOperator(p0: CtUnaryOperator<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtTypeAccess(p0: CtTypeAccess<T>?) {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> visitCtStatementList(p0: CtStatementList?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtFieldRead(p0: CtFieldRead<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtJavaDoc(p0: CtJavaDoc?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtClass(ctClass: CtClass<T>?) {
        if(ctClass == null) return
        // Annotations
        // Not implemented

        val modifiers = getModifiersMetadata(ctClass)
        adapter writeModifiers modifiers

        adapter write "class" and SPACE and ctClass.simpleName

        val inheritanceList = ArrayList<String>()
        if(ctClass.superclass != null && ctClass.superclass.qualifiedName != "kotlin.Any") {
            inheritanceList.add("${getTypeName(ctClass.superclass)}()") // TODO Primary constr call
        }
        if(ctClass.superInterfaces.isNotEmpty()) {
            ctClass.superInterfaces.forEach { inheritanceList.add(getTypeName(it)) }
        }
        if(inheritanceList.isNotEmpty())
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.OF_SUPERTYPE) and inheritanceList.joinToString()

        adapter write SPACE and LEFT_CURL
        adapter.newline()
        adapter.pushIndent()

        ctClass.typeMembers.forEach { it.accept(this) }

        adapter.popIndent()
        adapter writeln RIGHT_CURL
    }

    // TODO Replace with visitTypeRef
    private fun getTypeName(type : CtTypeReference<*>, fullyQualified : Boolean = true) : String {
        val prefix = type.`package`.qualifiedName
        return if(!fullyQualified || prefix.isEmpty()) type.simpleName
        else "${prefix}.${type.simpleName}"
    }

    override fun <T : Any?> visitCtInterface(p0: CtInterface<T>?) {

    }

    override fun visitCtPackageExport(p0: CtPackageExport?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtExecutableReference(p0: CtExecutableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtCatchVariable(p0: CtCatchVariable<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Enum<*>?> visitCtEnum(p0: CtEnum<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtCompilationUnit(p0: CtCompilationUnit?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtNewArray(p0: CtNewArray<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtComment(p0: CtComment?) {
        TODO("Not yet implemented")
    }

    override fun visitCtIf(ctIf: CtIf) {
        adapter write "if" and SPACE and LEFT_ROUND
        ctIf.condition.accept(this)
        adapter write RIGHT_ROUND and SPACE

        ctIf.getThenStatement<CtStatement>().accept(this)

        val elseStmt = ctIf.getElseStatement<CtStatement>()
        if(elseStmt != null) {
            adapter write SPACE and "else" and SPACE
            elseStmt.accept(this)
        }
        if(!adapter.onNewLine) adapter.newline()
    }

    override fun <T : Any?> visitCtFieldReference(p0: CtFieldReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtVariableRead(p0: CtVariableRead<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtCatch(p0: CtCatch?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, S : Any?> visitCtSwitchExpression(p0: CtSwitchExpression<T, S>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTypeParameter(p0: CtTypeParameter?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtConstructor(p0: CtConstructor<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTypeMemberWildcardImportReference(p0: CtTypeMemberWildcardImportReference?) {
        TODO("Not yet implemented")
    }

    override fun visitCtPackageDeclaration(p0: CtPackageDeclaration?) {
        TODO("Not yet implemented")
    }

    override fun visitCtThrow(p0: CtThrow?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtLocalVariableReference(p0: CtLocalVariableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTry(p0: CtTry?) {
        TODO("Not yet implemented")
    }

    override fun visitCtCodeSnippetStatement(p0: CtCodeSnippetStatement?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtArrayTypeReference(p0: CtArrayTypeReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtCatchVariableReference(p0: CtCatchVariableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtSuperAccess(p0: CtSuperAccess<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtPackageReference(p0: CtPackageReference?) {
        TODO("Not yet implemented")
    }

    override fun <A : Annotation?> visitCtAnnotation(p0: CtAnnotation<A>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtThisAccess(p0: CtThisAccess<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtAssert(p0: CtAssert<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtAnonymousExecutable(p0: CtAnonymousExecutable?) {
        TODO("Not yet implemented")
    }

    override fun visitCtDo(p0: CtDo?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtLiteral(literal: CtLiteral<T>) {
        adapter write (LiteralToStringHelper.getLiteralToken(literal))
    }

    override fun <T : Any?> visitCtBinaryOperator(p0: CtBinaryOperator<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtField(field: CtField<T>?) {
        if(field == null) return
        // Annotations
        // Not implemented

        // Modifiers
        val modifierSet : Set<KtModifierKind>? = getModifiersMetadata(field)
        val modifiers : Set<KtModifierKind> =
            modifierSet?.filterIf(KtModifierKind.OVERRIDE in modifierSet) { it != KtModifierKind.OPEN } ?:
            setOf(KtModifierKind.VAR)

        adapter writeModifiers modifiers

        // Name
        adapter write field.simpleName

        // Type
        val explicitType = (field.getMetadata(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE) as? Boolean?) ?: true
        if(explicitType || forceExplicitTypes) {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
            //visitCtTypeReference(field.type) //TODO
            adapter write getTypeName(field.type)
        }

        // Initializer or delegate
        visitDefaultExpr(field)
        adapter.newline()
    }

    private fun visitDefaultExpr(variable : CtVariable<*>) {
        if(variable.defaultExpression != null) {
            adapter write " = "
            variable.defaultExpression.accept(this)
        } else {
            val delegate = variable.getMetadata(KtMetadataKeys.PROPERTY_DELEGATE) as CtExpression<*>?
            if(delegate != null) {
                adapter write " by "
                delegate.accept(this)
            }
        }
    }

    override fun <T : Any?> visitCtLocalVariable(localVar: CtLocalVariable<T>) {
        val modifiers = getModifiersMetadata(localVar)
        adapter writeModifiers modifiers and localVar.simpleName

        if(!localVar.isInferred || forceExplicitTypes) {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
            //visitCtTypeReference(localVar.type) //TODO
            adapter write getTypeName(localVar.type)
        }

        // Initializer or delegate
        visitDefaultExpr(localVar)

        adapter.newline()
    }

    override fun <T : Any?, A : T> visitCtOperatorAssignment(p0: CtOperatorAssignment<T, A>?) {
        TODO("Not yet implemented")
    }

    override fun <S : Any?> visitCtCase(p0: CtCase<S>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtCodeSnippetExpression(p0: CtCodeSnippetExpression<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtUsedService(p0: CtUsedService?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, E : CtExpression<*>?> visitCtExecutableReferenceExpression(p0: CtExecutableReferenceExpression<T, E>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtProvidedService(p0: CtProvidedService?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTypeParameterReference(p0: CtTypeParameterReference?) {
        TODO("Not yet implemented")
    }

    override fun visitCtModuleRequirement(p0: CtModuleRequirement?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtEnumValue(p0: CtEnumValue<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtContinue(p0: CtContinue?) {
        TODO("Not yet implemented")
    }

    override fun visitCtFor(p0: CtFor?) {
        TODO("Not yet implemented")
    }

    override fun visitCtSynchronized(p0: CtSynchronized?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtParameter(param: CtParameter<T>) {
        val modifierSet = getModifiersMetadata(param)
        adapter writeModifiers modifierSet and param.simpleName
        adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
        adapter write getTypeName(param.type) // TODO visitType

        val defaultValue = param.getMetadata(KtMetadataKeys.PARAMETER_DEFAULT_VALUE) as? CtExpression<*>?
        if(defaultValue != null) {
            adapter write " = "
            defaultValue.accept(this)
        }
    }


    override fun <T : Any?> visitCtFieldWrite(p0: CtFieldWrite<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtAnnotationMethod(p0: CtAnnotationMethod<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTryWithResource(p0: CtTryWithResource?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtParameterReference(p0: CtParameterReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtModuleReference(p0: CtModuleReference?) {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> visitCtBlock(block: CtBlock<R>) {
        adapter write LEFT_CURL
        adapter.pushIndent()
        adapter.newline()

        block.statements.forEach { it.accept(this); adapter.newline() }

        adapter.popIndent()
        adapter write RIGHT_CURL
    }

    override fun <T : Any?> visitCtUnboundVariableReference(p0: CtUnboundVariableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtNewClass(p0: CtNewClass<T>?) {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> visitCtReturn(ctReturn: CtReturn<R>) {
        if(ctReturn.isImplicit && ctReturn.returnedExpression != null) {
            ctReturn.returnedExpression.accept(this)
        } else {
            adapter write "return"
            if(ctReturn.returnedExpression != null) {
                adapter write SPACE
                ctReturn.returnedExpression.accept(this)
            }
        }
    }

    override fun visitCtBreak(p0: CtBreak?) {
        TODO("Not yet implemented")
    }

    override fun visitCtWhile(p0: CtWhile?) {
        TODO("Not yet implemented")
    }

    override fun visitCtWildcardReference(p0: CtWildcardReference?) {
        TODO("Not yet implemented")
    }

    override fun visitCtImport(p0: CtImport?) {
        TODO("Not yet implemented")
    }

    override fun <S : Any?> visitCtSwitch(p0: CtSwitch<S>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, A : T> visitCtAssignment(p0: CtAssignment<T, A>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtJavaDocTag(p0: CtJavaDocTag?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtTypeReference(p0: CtTypeReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtVariableWrite(p0: CtVariableWrite<T>?) {
        TODO("Not yet implemented")
    }

    override fun <A : Annotation?> visitCtAnnotationType(p0: CtAnnotationType<A>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtModule(p0: CtModule?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtArrayWrite(p0: CtArrayWrite<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtArrayRead(p0: CtArrayRead<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtInvocation(p0: CtInvocation<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtMethod(method: CtMethod<T>?) {
        if(method == null) return
        // Annotations not implemented

        // Modifiers
        val modifierSet = getModifiersMetadata(method)
        val modifiers = modifierSet?.filterIf(KtModifierKind.OVERRIDE in modifierSet) { it != KtModifierKind.OPEN }
            ?: emptySet<KtModifierKind>()

        adapter writeModifiers modifiers and "fun " /* TODO Type params here */ and method.simpleName and LEFT_ROUND

        var commas = method.parameters.size-1
        method.parameters.forEach {
            it.accept(this)
            if(commas > 0) {
                commas--
                adapter write ", "
            }
        }

        adapter write RIGHT_ROUND

        // TODO If single block explicit type could be absent
        if(method.type.qualifiedName != "kotlin.Unit") {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
           // method.type.accept(this) // TODO
            adapter write getTypeName(method.type)
        }
        adapter write SPACE
        method.body.accept(this)

        adapter.newline()

    }

    override fun <T : Any?> visitCtAnnotationFieldAccess(p0: CtAnnotationFieldAccess<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtPackage(p0: CtPackage?) {
        TODO("Not yet implemented")
    }
}