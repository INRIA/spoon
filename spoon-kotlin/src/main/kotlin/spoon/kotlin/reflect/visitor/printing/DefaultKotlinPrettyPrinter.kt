package spoon.kotlin.reflect.visitor.printing

import spoon.experimental.CtUnresolvedImport
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.reference.*
import spoon.reflect.visitor.CtImportVisitor
import spoon.reflect.visitor.CtVisitor
import spoon.reflect.visitor.PrettyPrinter

class DefaultKotlinPrettyPrinter(
    private val adapter: DefaultPrinterAdapter,
    private val forceExplicitTypes: Boolean = false,
    private val topLvlClassName: String = "<top-level>",
    private val localFunctionWrapperName: String = "<local>"
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

    private fun CtConstructor<*>.isPrimary() : Boolean {
        return (getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as? Boolean?) == true
    }

    private fun CtElement.getBooleanMetadata(key: String) = getMetadata(key) as Boolean?

    private fun visitCommaSeparatedList(list : List<CtElement>) {
        var commas = list.size-1
        list.forEach {
            it.accept(this)
            if(commas > 0) {
                commas--
                adapter write ", "
            }
        }
    }

    private fun visitArgumentList(list: List<CtExpression<*>>) {
        if(list.isEmpty()) {
            adapter write LEFT_ROUND and RIGHT_ROUND
            return
        }

        // Check if a lambda can be moved out of parenthesis if it's the last argument
        fun CtElement.isMovableLambda(): Boolean {
            return this is CtLambda<*> &&
                    this.getBooleanMetadata(KtMetadataKeys.LAMBDA_AS_ANONYMOUS_FUNCTION) == false &&
                    this.getMetadata(KtMetadataKeys.NAMED_ARGUMENT) as String? == null
        }

        if(list.size == 1 && list[0].isMovableLambda()) {
            adapter write SPACE
            list[0].accept(this)
            return
        }

        fun CtExpression<*>.acceptPossiblyNamed() {
            val name = getMetadata(KtMetadataKeys.NAMED_ARGUMENT) as String?
            if(name != null) {
                adapter write name and " = "
            }
            this.accept(this@DefaultKotlinPrettyPrinter)
        }

        adapter write LEFT_ROUND
        for(i in 0 until list.size-1) {
            list[i].acceptPossiblyNamed()
            adapter write ", "
        }
        val closeParBefore = list.last().isMovableLambda()
        if(closeParBefore) {
            adapter write RIGHT_ROUND and SPACE
        }
        list.last().acceptPossiblyNamed()
        if(!closeParBefore) {
            adapter write RIGHT_ROUND
        }
    }

    private fun exitCtExpression(e: CtExpression<*>) {
        if(e.typeCasts.isNotEmpty()) {
            e.typeCasts.forEach {
                val token = if(it.getMetadata(KtMetadataKeys.TYPE_CAST_AS_SAFE) as Boolean) "as?"
                else "as"
                adapter write " $token "
                it.accept(this)
            }
            adapter write ')'
        } else if(shouldAddPar(e)) {
            adapter write ')'
        }
        if((e.getMetadata(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL) as? Boolean?) == true) {
            adapter write "!!"
        }
    }

    private fun enterCtExpression(e: CtExpression<*>) {
        if(shouldAddPar(e)) {
            adapter write '('
        }
    }

    private fun shouldAddPar(e: CtExpression<*>): Boolean {
        if(e.typeCasts.isNotEmpty()) return true
        if((e.getMetadata(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL) as? Boolean?) == true) return true
        return when(e.parent) {
            null -> false
            is CtBinaryOperator<*>, is CtUnaryOperator<*> ->
                e is CtAssignment<*,*> || e is CtUnaryOperator<*> || e is CtBinaryOperator<*>
            is CtTargetedExpression<*,*> -> {
                e.parent.target == e && (e is CtBinaryOperator<*> || e is CtAssignment<*,*> || e is CtUnaryOperator<*>)
            }
            else -> false
        }
    }

    private fun enterCtStatement(s: CtStatement) {
        if(s.label != null) {
            adapter write s.label and '@' and SPACE
        }
    }

    private fun exitCtStatement(s: CtStatement) {
        // Do nothing
    }


    @Suppress("UNCHECKED_CAST")
    private fun getModifiersMetadata(e : CtElement) : Set<KtModifierKind>? =
        e.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>

    override fun getResult(): String {
        return adapter.toString()
    }

    override fun prettyprint(e: CtElement): String {
        adapter.reset()
        e.accept(this)
        val s = adapter.toString()
        adapter.reset()
        return s
    }

    override fun printElement(element: CtElement): String {
        // Ugly but good enough for now, avoid changing state in current printer
        return DefaultKotlinPrettyPrinter(DefaultPrinterAdapter()).prettyprint(element)
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

    override fun <T : Any?> visitCtLambda(ctLambda: CtLambda<T>) {
        if(ctLambda.getBooleanMetadata(KtMetadataKeys.LAMBDA_AS_ANONYMOUS_FUNCTION) == true) {
            visitAnonymousFunction(ctLambda)
        } else {
            adapter write LEFT_CURL and SPACE
            val params = ctLambda.parameters.filterNot { it.isImplicit }
            if (params.isNotEmpty()) {
                visitCommaSeparatedList(params)
                adapter write " -> "
            }

            ctLambda.body?.let { visitStatementList(it.statements, inlineSingleStatement = true) }
            adapter write RIGHT_CURL
        }
    }

    private fun visitAnonymousFunction(ctLambda: CtLambda<*>) {
        adapter write "fun" and LEFT_ROUND
        visitCommaSeparatedList(ctLambda.parameters)
        adapter write RIGHT_ROUND
        if(ctLambda.type.qualifiedName != "kotlin.Unit") {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
            ctLambda.type.accept(this)
        }
        adapter write SPACE
        if(ctLambda.body.isImplicit) {
            adapter write "= "
        }
        ctLambda.body.accept(this)
    }

    override fun visitCtForEach(forEach: CtForEach) {
        enterCtStatement(forEach)
        adapter write "for" and SPACE and LEFT_ROUND
        forEach.variable.accept(this)
        adapter write " in "
        forEach.expression.accept(this)
        adapter write RIGHT_ROUND
        adapter.ensureSpaceOrNewlineBeforeNext()
        forEach.body.accept(this)
        exitCtStatement(forEach)
    }

    override fun <T : Any?> visitCtConstructorCall(constrCall: CtConstructorCall<T>?) {
        TODO()
    }

    override fun <T : Any?> visitCtUnaryOperator(unaryOperator: CtUnaryOperator<T>) {
        enterCtExpression(unaryOperator)
        val kind = unaryOperator.kind
        when(kind) {
            UnaryOperatorKind.POSTDEC,
            UnaryOperatorKind.POSTINC -> {
                unaryOperator.operand.accept(this)
                adapter write UnaryOperatorStringHelper.asToken(kind)
            }
            else -> {
                adapter write UnaryOperatorStringHelper.asToken(kind)
                unaryOperator.operand.accept(this)
            }
        }
        exitCtExpression(unaryOperator)
    }

    override fun <T : Any?> visitCtTypeAccess(typeAccess: CtTypeAccess<T>) {
        if(typeAccess.isImplicit) return
        enterCtExpression(typeAccess)
        typeAccess.accessedType.accept(this)
        exitCtExpression(typeAccess)
    }

    override fun <R : Any?> visitCtStatementList(p0: CtStatementList?) {
        TODO("Not yet implemented")
    }

    override fun visitCtJavaDoc(p0: CtJavaDoc?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtClass(ctClass: CtClass<T>?) {
        if(ctClass == null) return
        if(ctClass.isImplicit) {
           when(ctClass.simpleName) {
               topLvlClassName -> { // Print the top level class
                   ctClass.typeMembers.filter { it is CtMethod<*> || it is CtField<*> }.forEach {
                       it.accept(this)
                   }
                   return
               }
               localFunctionWrapperName -> { // Just print the wrapped method
                   ctClass.methods.forEach { it.accept(this) }
                   return
               }
           }
        }
        adapter.ensureNEmptyLines(1)
        // Annotations
        // Not implemented

        val modifiers = getModifiersMetadata(ctClass)
        adapter writeModifiers modifiers

        adapter write "class" and SPACE and ctClass.simpleName

        val typeParamHandler = TypeParameterHandler(ctClass, this, false)
        if(!typeParamHandler.isEmpty) {
            adapter write typeParamHandler.generateTypeParamListString() and SPACE
        }

        val inheritanceList = ArrayList<String>()

        val primaryConstructor = ctClass.constructors.firstOrNull { it.isPrimary() }
        if(primaryConstructor != null) {
            visitCtConstructor(primaryConstructor)
        } else if(ctClass.superclass != null) {
            inheritanceList.add(getTypeName(ctClass.superclass))
        }
       // if(ctClass.superclass != null && ctClass.superclass.qualifiedName != "kotlin.Any") {
       //     inheritanceList.add("${getTypeName(ctClass.superclass)}()") // TODO Primary constr call
       // }
        if(ctClass.superInterfaces.isNotEmpty()) {
            ctClass.superInterfaces.forEach { inheritanceList.add(getTypeName(it)) }
        }
        if(inheritanceList.isNotEmpty()) {
            var p = ""
            if(ctClass.superclass == null || ctClass.superclass.qualifiedName == "kotlin.Any") {
                adapter.writeColon(DefaultPrinterAdapter.ColonContext.OF_SUPERTYPE)
            }
            else p = ", "
            adapter write inheritanceList.joinToString(prefix = p)
        }

        val whereClause = typeParamHandler.generateWhereClause()
        if(whereClause.isNotEmpty()) {
            adapter write " where "
            adapter.writeAligned(whereClause)
        }

        adapter write SPACE and LEFT_CURL
        adapter.newline()
        adapter.pushIndent()

      //  ctClass.constructors.filterNot { it.isPrimary() }.forEach { it.accept(this) }
        ctClass.typeMembers.filterNot { it is CtConstructor<*> && it.isPrimary() }.forEach {
           if(!it.isImplicit) { it.accept(this) }
        }
        adapter.popIndent()
        adapter.ensureNEmptyLines(0)
        adapter writeln RIGHT_CURL
    }

    override fun <T : Any?> visitCtConstructor(ctConstructor : CtConstructor<T>) {
        // Annotations not implemented

        val primary = ctConstructor.getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as? Boolean? ?: false
        if(ctConstructor.isImplicit && !primary) return

        val modifierSet = getModifiersMetadata(ctConstructor)
        if(primary) {
            if(modifierSet != null && modifierSet.filterNot { it == KtModifierKind.PUBLIC }.isNotEmpty()) {
                adapter write SPACE
                adapter writeModifiers modifierSet
                adapter write "constructor"
            }
        } else {
            adapter.ensureNEmptyLines(1)
            adapter writeModifiers modifierSet and "constructor"
        }
        adapter write LEFT_ROUND
        visitCommaSeparatedList(ctConstructor.parameters)
        adapter write RIGHT_ROUND

        val delegatedConstr = ctConstructor.getMetadata(KtMetadataKeys.CONSTRUCTOR_DELEGATE_CALL) as? CtInvocation<Any>?
        visitCtInvocation(delegatedConstr)

        if(!primary) {
            if(ctConstructor.body != null) {
                adapter write SPACE
                ctConstructor.body?.accept(this)
            }
        }
    }

    override fun <T : Any?> visitCtInterface(p0: CtInterface<T>?) {

    }

    override fun visitCtPackageExport(p0: CtPackageExport?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtExecutableReference(p0: CtExecutableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Enum<*>?> visitCtEnum(p0: CtEnum<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtCompilationUnit(compilationUnit: CtCompilationUnit) {
        compilationUnit.packageDeclaration?.accept(this)
        adapter.ensureNEmptyLines(1)
        compilationUnit.imports.forEach { it.accept(this) }
        compilationUnit.declaredTypes.forEach { it.accept(this) }
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
            adapter write " else "
            elseStmt.accept(this)
        }
    }

    override fun <T : Any?> visitCtFieldReference(fieldRef: CtFieldReference<T>) {
        adapter write fieldRef.simpleName
    }

    override fun <T : Any?> visitCtVariableRead(varRead: CtVariableRead<T>) {
        enterCtExpression(varRead)
        varRead.variable.accept(this)
        exitCtExpression(varRead)
    }

    override fun visitCtTry(ctTry: CtTry) {
        adapter write "try" and SPACE
        ctTry.body.accept(this)
        ctTry.catchers.forEach { it.accept(this) }
        val finalizer = ctTry.finalizer
        if(finalizer != null) {
            adapter write " finally "
            finalizer.accept(this)
        }
    }

    override fun visitCtCatch(ctCatch: CtCatch) {
        adapter write SPACE and "catch" and SPACE and LEFT_ROUND
        ctCatch.parameter.accept(this)
        adapter write RIGHT_ROUND and SPACE
        ctCatch.body.accept(this)
    }

    override fun <T : Any?> visitCtCatchVariable(catchVariable: CtCatchVariable<T>) {
        adapter write catchVariable.simpleName
        adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
        catchVariable.type.accept(this)
    }

    override fun <T : Any?, S : Any?> visitCtSwitchExpression(p0: CtSwitchExpression<T, S>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTypeParameter(typeParam: CtTypeParameter) {
        val modifiers = typeParam.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>?
        val modifiersString = if(modifiers != null && modifiers.isNotEmpty()) {
            modifiers.toList().sorted().joinToString(separator = " ", postfix = " ", transform = { it.token })
        } else {
            ""
        }

        adapter write modifiersString and typeParam.simpleName

        if(typeParam.superclass != null) {
            typeParam.superclass.accept(this)
        }
    }

    override fun visitCtTypeMemberWildcardImportReference(p0: CtTypeMemberWildcardImportReference?) {
        TODO("Not yet implemented")
    }

    override fun visitCtPackageDeclaration(ctPackage: CtPackageDeclaration) {
        if(!ctPackage.reference.isUnnamedPackage) {
            adapter write "package" and SPACE and ctPackage.reference.qualifiedName
        }
    }

    override fun visitCtThrow(ctThrow: CtThrow) {
        enterCtStatement(ctThrow)
        adapter write "throw" and SPACE
        ctThrow.thrownExpression.accept(this)
        exitCtStatement(ctThrow)
    }

    override fun <T : Any?> visitCtLocalVariableReference(localVarRef: CtLocalVariableReference<T>) {
        adapter write localVarRef.simpleName
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

    override fun <T : Any?> visitCtSuperAccess(superAccess: CtSuperAccess<T>) {
        if(superAccess.isImplicit) return
        enterCtExpression(superAccess)
        // Super access as selector is illegal (x.super.y), so no target has to be checked
        adapter write "super"
        exitCtExpression(superAccess)
    }

    override fun visitCtPackageReference(pkgRef: CtPackageReference) {
        adapter write pkgRef.qualifiedName
    }

    override fun <A : Annotation?> visitCtAnnotation(p0: CtAnnotation<A>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtThisAccess(thisAccess: CtThisAccess<T>) {
        if(thisAccess.isImplicit) return
        enterCtExpression(thisAccess)
        adapter write "this"
        exitCtExpression(thisAccess)
    }

    override fun <T : Any?> visitCtAssert(p0: CtAssert<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtAnonymousExecutable(ctAnonExec: CtAnonymousExecutable) {
        adapter.ensureNEmptyLines(1)
        adapter write "init "
        ctAnonExec.body.accept(this)
    }

    override fun visitCtDo(ctDo: CtDo) {
        enterCtStatement(ctDo)
        adapter write "do "
        ctDo.body.accept(this)
        adapter write " while " and LEFT_ROUND
        ctDo.loopingExpression.accept(this)
        adapter write RIGHT_ROUND
        exitCtStatement(ctDo)
    }

    override fun <T : Any?> visitCtLiteral(literal: CtLiteral<T>) {
        enterCtExpression(literal)
        adapter write (LiteralToStringHelper.getLiteralToken(literal))
        exitCtExpression(literal)
    }

    override fun <T : Any?> visitCtBinaryOperator(binOp: CtBinaryOperator<T>) {
        enterCtExpression(binOp)
        binOp.leftHandOperand.accept(this)
        val operator = binOp.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as? KtBinaryOperatorKind
        if(operator == null) {
            adapter write KtBinaryOperatorKind.fromJavaOperatorKind(binOp.kind).asToken()
        } else {
            adapter write operator.asToken()
        }
        binOp.rightHandOperand.accept(this)
        exitCtExpression(binOp)
    }

    override fun <T : Any?> visitCtField(field: CtField<T>?) {
        if(field == null || field.isImplicit) return
        adapter.ensureNEmptyLines(0)

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
            visitCtTypeReference(field.type)
        }

        // Initializer or delegate
        adapter.pushIndent()
        visitDefaultExpr(field)
        adapter.popIndent()
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
            visitCtTypeReference(localVar.type)
        }

        // Initializer or delegate
        visitDefaultExpr(localVar)
    }

    override fun <T : Any?, A : T> visitCtOperatorAssignment(opAssignment: CtOperatorAssignment<T, A>) {
        opAssignment.assigned.accept(this)
        adapter write " ${KtBinaryOperatorKind.fromJavaOperatorKind(opAssignment.kind).asString}= "
        opAssignment.assignment.accept(this)
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

    override fun visitCtTypeParameterReference(typeParam: CtTypeParameterReference) {
        val name = TypeName.build(typeParam)
        if(typeParam.isSimplyQualified) {
            adapter write name.simpleNameWithNullability
        } else {
            adapter write name.fQNameWithNullability
        }
    }

    override fun visitCtModuleRequirement(p0: CtModuleRequirement?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtEnumValue(p0: CtEnumValue<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtFor(p0: CtFor?) {
        TODO("Not yet implemented")
    }

    override fun visitCtSynchronized(p0: CtSynchronized?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtParameter(param: CtParameter<T>) {
        if(param.isImplicit) return
        val modifierSet = getModifiersMetadata(param)
        adapter writeModifiers modifierSet and param.simpleName
        adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
        param.type.accept(this)

        val defaultValue = param.getMetadata(KtMetadataKeys.PARAMETER_DEFAULT_VALUE) as? CtExpression<*>?
        if(defaultValue != null) {
            adapter write " = "
            defaultValue.accept(this)
        }
    }


    override fun <T : Any?> visitCtFieldWrite(fieldWrite: CtFieldWrite<T>) = visitFieldAccess(fieldWrite)

    override fun <T : Any?> visitCtFieldRead(fieldRead: CtFieldRead<T>) = visitFieldAccess(fieldRead)

    private fun visitFieldAccess(fieldAccess: CtFieldAccess<*>) {
        enterCtExpression(fieldAccess)
        visitTarget(fieldAccess)
        fieldAccess.variable.accept(this)
        exitCtExpression(fieldAccess)
    }

    private fun visitTarget(expr: CtTargetedExpression<*,*>) {
        if(expr.target != null && !expr.target.isImplicit) {
            expr.target.accept(this)
            if((expr.getMetadata(KtMetadataKeys.ACCESS_IS_SAFE) as? Boolean?) == true) {
                adapter write '?'
            }
            adapter write '.'
        }
    }


    override fun <T : Any?> visitCtAnnotationMethod(p0: CtAnnotationMethod<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTryWithResource(p0: CtTryWithResource?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtParameterReference(parameterRef: CtParameterReference<T>) {
        adapter write parameterRef.simpleName
    }

    override fun visitCtModuleReference(p0: CtModuleReference?) {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> visitCtBlock(block: CtBlock<R>) {
        enterCtStatement(block)
        if(block.isImplicit && block.statements.size == 1) {
            block.statements[0].accept(this)
            return
        }
        adapter write LEFT_CURL

        visitStatementList(block.statements)

        adapter.ensureNEmptyLines(0)
        adapter write RIGHT_CURL
        exitCtStatement(block)
    }

    private fun visitStatementList(statements: List<CtStatement>,
                           inlineSingleStatement: Boolean = false
    ) {
        if(statements.size <= 1 && inlineSingleStatement) {
            statements.getOrNull(0)?.accept(this)
            adapter write SPACE
        } else {
            adapter.pushIndent()
            adapter.newline()
            statements.forEach { it.accept(this); adapter.newline() }
            adapter.popIndent()
        }
    }

    override fun <T : Any?> visitCtUnboundVariableReference(p0: CtUnboundVariableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtNewClass(p0: CtNewClass<T>?) {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> visitCtReturn(ctReturn: CtReturn<R>) {
        if(ctReturn.isImplicit && ctReturn.returnedExpression != null) { // FIXME Correct?
            ctReturn.returnedExpression.accept(this)
        } else {
            adapter write "return"
            if(ctReturn.label != null) {
                adapter write '@' and ctReturn.label
            }
            if(ctReturn.returnedExpression != null) {
                adapter write SPACE
                ctReturn.returnedExpression.accept(this)
            }
        }
    }

    override fun visitCtBreak(ctBreak: CtBreak) {
        enterCtStatement(ctBreak)
        adapter write "break"
        if(ctBreak.targetLabel != null) {
            adapter write '@' and ctBreak.targetLabel
        }
        exitCtStatement(ctBreak)
    }

    override fun visitCtContinue(ctContinue: CtContinue) {
        enterCtStatement(ctContinue)
        adapter write "continue"
        if(ctContinue.targetLabel != null) {
            adapter write '@' and ctContinue.targetLabel
        }
        exitCtStatement(ctContinue)
    }

    override fun visitCtWhile(ctWhile: CtWhile) {
        enterCtStatement(ctWhile)
        adapter write "while" and SPACE and LEFT_ROUND
        ctWhile.loopingExpression.accept(this)
        adapter write RIGHT_ROUND and SPACE
        ctWhile.body.accept(this)
        exitCtStatement(ctWhile)
    }

    override fun visitCtWildcardReference(wildcardReference: CtWildcardReference) {
        adapter write '*'
    }

    override fun visitCtImport(ctImport: CtImport) {
        if(ctImport.importKind != null) {
            adapter.ensureNEmptyLines(0)
            adapter write "import" and SPACE
            ctImport.accept(object : CtImportVisitor {
                override fun <T : Any?> visitTypeImport(typeRef: CtTypeReference<T>) {
                    visitCtTypeReference(typeRef)
                }

                override fun <T : Any?> visitUnresolvedImport(unresolvedImport: CtUnresolvedImport) {
                    adapter write unresolvedImport.unresolvedReference
                }

                override fun <T : Any?> visitMethodImport(execRef: CtExecutableReference<T>) {
                    if(execRef.declaringType.`package` != null) {
                        visitCtPackageReference(execRef.declaringType.`package`)
                        adapter write '.'
                    }
                    adapter write execRef.simpleName
                }

                override fun <T : Any?> visitFieldImport(fieldRef: CtFieldReference<T>) {
                    if(fieldRef.declaringType.`package` != null) {
                        visitCtPackageReference(fieldRef.declaringType.`package`)
                        adapter write '.'
                    }
                    adapter write fieldRef.simpleName
                }

                override fun visitAllTypesImport(pkgRef: CtPackageReference) {
                    visitCtPackageReference(pkgRef)
                    adapter write '.' and '*'
                }

                override fun <T : Any?> visitAllStaticMembersImport(typeRef: CtTypeMemberWildcardImportReference) {
                    visitCtTypeReference(typeRef.typeReference)
                    adapter write '.' and '*'
                }

            })
            (ctImport.getMetadata(KtMetadataKeys.IMPORT_ALIAS) as String?)?.let { adapter write " as " and it }
        }
    }

    override fun <S : Any?> visitCtSwitch(p0: CtSwitch<S>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, A : T> visitCtAssignment(ctAssignment: CtAssignment<T, A>?) {
        if(ctAssignment == null) return
        ctAssignment.assigned.accept(this)
        adapter write " = "
        ctAssignment.assignment.accept(this)
    }

    override fun visitCtJavaDocTag(p0: CtJavaDocTag?) {
        TODO("Not yet implemented")
    }

    private fun getTypeName(type: CtTypeReference<*>,
                            fullyQualified: Boolean = true,
                            ignoreNullability: Boolean = false
    ) : String {
        val prefix = if(fullyQualified) type.`package`?.qualifiedName ?: "" else ""
        val nullable = type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as? Boolean? ?: false
        val suffix = if(nullable && !ignoreNullability) "?" else ""
        return if(!fullyQualified || prefix.isEmpty()) "${type.simpleName}$suffix"
        else "${prefix}.${type.simpleName}${suffix}"
    }

    override fun <T : Any?> visitCtTypeReference(typeRef: CtTypeReference<T>) {
        if(typeRef.isImplicit) return
        if(typeRef.declaringType != null) {
            typeRef.accessType.accept(this)
            adapter write '.'
        }
        adapter write getTypeName(typeRef)
    }

    override fun <T : Any?> visitCtVariableWrite(varWrite: CtVariableWrite<T>) {
        adapter write varWrite.variable.simpleName
    }

    override fun <A : Annotation?> visitCtAnnotationType(p0: CtAnnotationType<A>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtModule(p0: CtModule?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtArrayWrite(arrayWrite: CtArrayWrite<T>) = visitCtArrayAccess(arrayWrite)

    override fun <T : Any?> visitCtArrayRead(arrayRead: CtArrayRead<T>) = visitCtArrayAccess(arrayRead)

    private fun visitCtArrayAccess(arrayAccess: CtArrayAccess<*,*>) {
        arrayAccess.target.accept(this)
        adapter write LEFT_SQUARE
        visitCommaSeparatedList(arrayAccess.getMetadata(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS) as List<CtElement>)
        adapter write RIGHT_SQUARE
    }

    override fun <T : Any?> visitCtInvocation(invocation: CtInvocation<T>?) {
        if(invocation == null || invocation.isImplicit) return
        if(invocation.getMetadata(KtMetadataKeys.INVOCATION_IS_INFIX) as Boolean? == true) {
            return visitInfixInvocation(invocation)
        }
        enterCtExpression(invocation)
        if(invocation.executable.isConstructor) {
            val parentType = invocation.getParent(CtType::class.java)
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.CONSTRUCTOR_DELEGATION)
            if(parentType == null || parentType.qualifiedName == invocation.executable.declaringType.qualifiedName) {
                adapter write "this"
            } else {
                val primary = invocation.parent.getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as? Boolean?
                if(primary == true) {
                        invocation.type.accept(this)
                }
                else {
                    adapter write "super"
                }
            }
        } else {
            var separator = ""
            if(invocation.target != null && !invocation.target.isImplicit) {
                invocation.target.accept(this)
                val isSafe = invocation.getMetadata(KtMetadataKeys.ACCESS_IS_SAFE) as Boolean?
                separator = if(isSafe == true) "?." else "."
            }
            if(!shouldIgnoreIdentifier(invocation)) { // If invoke operator, the name of the called function is omitted
                adapter write separator
                adapter write invocation.executable.simpleName
            }
        }

        visitArgumentList(invocation.arguments) // Paren handled in call

        exitCtExpression(invocation)
    }

    private fun shouldIgnoreIdentifier(invocation: CtInvocation<*>): Boolean {
        if(invocation.executable.simpleName == "invoke") {
            val asOperator = invocation.getMetadata(KtMetadataKeys.INVOKE_AS_OPERATOR) as Boolean?
            return asOperator == true
        }
        return false
    }

    private fun <T> visitInfixInvocation(ctInvocation: CtInvocation<T>) {
        enterCtExpression(ctInvocation)
        ctInvocation.target.accept(this)
        adapter write " ${ctInvocation.executable.simpleName} "
        ctInvocation.arguments[0].accept(this)
        exitCtExpression(ctInvocation)
    }

    override fun <T : Any?> visitCtMethod(method: CtMethod<T>?) {
        if(method == null) return
        // Annotations not implemented

        adapter.ensureNEmptyLines(1)

        // Modifiers
        val modifierSet = getModifiersMetadata(method)
        val modifiers = modifierSet?.filterIf(KtModifierKind.OVERRIDE in modifierSet) { it != KtModifierKind.OPEN }
            ?: emptySet<KtModifierKind>()

        adapter writeModifiers modifiers and "fun"

        val typeParamHandler = TypeParameterHandler(method, this, false)
        adapter write typeParamHandler.generateTypeParamListString() and SPACE

        val extensionTypeRef = method.getMetadata(KtMetadataKeys.EXTENSION_TYPE_REF) as CtTypeAccess<*>?
        if(extensionTypeRef != null) {
            extensionTypeRef.accept(this)
            adapter write '.'
        }

        adapter write method.simpleName and LEFT_ROUND
        visitCommaSeparatedList(method.parameters)
        adapter write RIGHT_ROUND

        if(method.type.qualifiedName != "kotlin.Unit") {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
            method.type.accept(this)
        }

        val whereClause = typeParamHandler.generateWhereClause()
        if(whereClause.isNotEmpty()) {
            adapter write " where "
            adapter.writeAligned(whereClause)
        }

        if(method.body != null) {
            adapter.ensureSpaceOrNewlineBeforeNext()
            if(method.body.isImplicit) {
                adapter write "= "
            }
            method.body.accept(this)
        }
    }

    override fun <T : Any?> visitCtAnnotationFieldAccess(p0: CtAnnotationFieldAccess<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtPackage(p0: CtPackage?) {
        TODO("Not yet implemented")
    }
}