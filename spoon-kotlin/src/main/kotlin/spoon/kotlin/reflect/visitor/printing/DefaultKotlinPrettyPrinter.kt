package spoon.kotlin.reflect.visitor.printing

import spoon.experimental.CtUnresolvedImport
import spoon.kotlin.compiler.ir.SpoonIrBuildException
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
    private val localFunctionWrapperName: String = "<local>",
    private val arrowSyntaxForFunctionType: Boolean = true
    //     private val sourceCompilationUnit : CtCompilationUnit
) : CtVisitor, PrettyPrinter {

    private val LEFT_ROUND = '('
    private val RIGHT_ROUND = ')'
    private val LEFT_SQUARE = '['
    private val RIGHT_SQUARE = ']'
    private val LEFT_CURL = '{'
    private val RIGHT_CURL = '}'
    private val LEFT_ANGLE = '<'
    private val RIGHT_ANGLE = '>'
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
    private fun CtElement.getBooleanMetadata(key: String, default: Boolean) =
        getMetadata(key) as Boolean? ?: default

    private fun <T: CtElement> visitCommaSeparatedList(
        list : List<T>,
        specialAction: ((T) -> Unit)? = null) {
        var commas = list.size-1
        for (it in list) {
            if(specialAction == null) {
                it.accept(this)
            } else {
                specialAction(it)
            }
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
                adapter writeIdentifier name and " = "
            }
            if(this.getBooleanMetadata(KtMetadataKeys.SPREAD, default = false)) {
                adapter write '*'
            }
            this.accept(this@DefaultKotlinPrettyPrinter)
        }

        adapter write LEFT_ROUND
        for(i in 0 until list.size-1) {
            if(i > 0) adapter write ", "
            list[i].acceptPossiblyNamed()
        }
        val closeParBefore = list.last().isMovableLambda()
        if(closeParBefore) {
            adapter write RIGHT_ROUND and SPACE
        } else if(list.size > 1) {
            adapter write ", "
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
        if(e.getBooleanMetadata(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL, false)) {
            adapter write "!!"
        }
        if(e.getBooleanMetadata(KtMetadataKeys.IS_CLASS_REFERENCE, false)) {
            adapter write "::class"
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
            is CtForEach -> false
            is CtBinaryOperator<*>, is CtUnaryOperator<*> ->
                e is CtAssignment<*,*> || e is CtUnaryOperator<*> || e is CtBinaryOperator<*>
            is CtTargetedExpression<*,*> -> {
                e.parent.target == e && (e is CtBinaryOperator<*>
                        || e is CtAssignment<*,*>
                        || e is CtUnaryOperator<*>
                        || (e is CtInvocation<*> && e.getBooleanMetadata(KtMetadataKeys.INVOCATION_IS_INFIX, false)))
            }
            else -> false
        }
    }

    private fun enterCtStatement(s: CtStatement) {
        val label = s.label ?:
            if(s is CtReturn<*>) null
            else s.getMetadata(KtMetadataKeys.LABEL) as String?
        if(label != null) {
            adapter write label and '@' and SPACE
        }
    }

    private fun exitCtStatement(s: CtStatement) {
        // Do nothing
    }


    @Suppress("UNCHECKED_CAST")
    private fun getModifiersMetadata(e : CtElement) : Set<KtModifierKind> =
        e.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>? ?: emptySet()

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
            val params = ctLambda.parameters.filterNot {
                it.isImplicit && !it.getBooleanMetadata(KtMetadataKeys.IS_DESTRUCTURED, false)
            }
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

    override fun <T : Any?> visitCtConstructorCall(constructorCall: CtConstructorCall<T>) {
        val parentType = constructorCall.getParent(CtType::class.java)
        adapter.writeColon(DefaultPrinterAdapter.ColonContext.CONSTRUCTOR_DELEGATION)
        if(parentType == null || parentType.qualifiedName == constructorCall.executable.declaringType.qualifiedName) {
            adapter write "this"
        } else {
            val parentConstructor = constructorCall.getParent(CtConstructor::class.java) ?:
                throw SpoonIrBuildException("""ConstructorCall without CtConstructor parent. 
                        |CtConstructorCall is used as constructor delegate calls. Use CtInvocation for normal constructor invocations.
                    """.trimMargin())
            val primary = parentConstructor.getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as Boolean?
            if(primary == true) {
                constructorCall.type.accept(this)
            } else {
                adapter write "super"
            }
        }

        visitArgumentList(constructorCall.arguments)
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

    private fun visitTypeAlias(ctClass: CtClass<*>) {
        val alias = ctClass.getMetadata(KtMetadataKeys.TYPE_ALIAS) as CtTypeReference<*>
        adapter.ensureNEmptyLines(1)

        adapter writeModifiers getModifiersMetadata(ctClass) and "typealias " and ctClass.simpleName
        val typeParamHandler = TypeParameterHandler(ctClass, this, false)
        if(!typeParamHandler.isEmpty) {
            adapter write typeParamHandler.generateTypeParamListString()
        }

        adapter write " = "
        alias.accept(this)
    }

    override fun <T : Any?> visitCtClass(ctClass: CtClass<T>?) {
        if(ctClass == null) return
        if(ctClass.getMetadata(KtMetadataKeys.TYPE_ALIAS) != null) {
            visitTypeAlias(ctClass)
            return
        }
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
        writeAnnotations(ctClass)
        adapter.ensureNEmptyLines(0)

        enterCtStatement(ctClass)
        val modifiers = getModifiersMetadata(ctClass)
        adapter writeModifiers modifiers
        if(ctClass.getBooleanMetadata(KtMetadataKeys.CLASS_IS_OBJECT) == true) {
            visitObject(ctClass, false)
            return
        }

        adapter write "class" and SPACE and ctClass.simpleName

        val typeParamHandler = TypeParameterHandler(ctClass, this, false)
        if(!typeParamHandler.isEmpty) {
            adapter write typeParamHandler.generateTypeParamListString() and SPACE
        }

        writeInheritanceList(ctClass)

        val whereClause = typeParamHandler.generateWhereClause()
        if(whereClause.isNotEmpty()) {
            adapter write " where "
            adapter.writeAligned(whereClause)
        }

        writeTypeMembersWithoutPrimaryConstructor(ctClass)
        exitCtStatement(ctClass)
    }

    private fun writeTypeMembersWithoutPrimaryConstructor(ctType: CtType<*>) {
        adapter write SPACE and LEFT_CURL
        adapter.newline()
        adapter.pushIndent()

        ctType.typeMembers.filterNot { it is CtConstructor<*> && it.isPrimary() }.forEach {
            if(!it.isImplicit) { it.accept(this) }
        }
        adapter.popIndent()
        adapter.ensureNEmptyLines(0)
        adapter writeln RIGHT_CURL
    }

    private fun writeInheritanceList(ctType: CtType<*>) {
        val printSuperTypes = KtModifierKind.ANNOTATION !in getModifiersMetadata(ctType)
        if(ctType is CtClass<*>) {
            val primaryConstructor = ctType.constructors.firstOrNull { it.isPrimary() }
            if (primaryConstructor != null) {
                visitCtConstructor(primaryConstructor, printSuperTypes)
            }
        }

        if(printSuperTypes && ctType.superInterfaces.isNotEmpty()) {
            if(ctType !is CtInterface<*> && ctType.superclass != null && ctType.superclass.qualifiedName != "kotlin.Any") {
                adapter write ", "
            } else {
                adapter.writeColon(DefaultPrinterAdapter.ColonContext.OF_SUPERTYPE)
            }
            visitCommaSeparatedList(ctType.superInterfaces.toList()) { superInterface ->
                val delegate = superInterface.getMetadata(KtMetadataKeys.SUPER_TYPE_DELEGATE) as CtElement?
                superInterface.accept(this)
                if(delegate != null) {
                    adapter write " by "
                    delegate.accept(this)
                }
            }
        }
    }

    private fun visitObject(ctClass: CtClass<*>, anonymous: Boolean) {
        adapter write "object"
        if(!anonymous) adapter write SPACE and ctClass.simpleName

        writeInheritanceList(ctClass)

        writeTypeMembersWithoutPrimaryConstructor(ctClass)
    }

    override fun <T : Any?> visitCtInterface(ctInterface: CtInterface<T>) {
        adapter.ensureNEmptyLines(1)
        // Annotations
        // Not yet implemented

        // Modifiers
        // Interfaces are abstract, but it shouldn't be printed
        val modifiers = getModifiersMetadata(ctInterface).filterNot { it == KtModifierKind.ABSTRACT }
        adapter writeModifiers modifiers

        // Name
        adapter write "interface" and SPACE and ctInterface.simpleName

        // Type parameters list
        val typeParamHandler = TypeParameterHandler(ctInterface, this, false)
        if(!typeParamHandler.isEmpty) {
            adapter write typeParamHandler.generateTypeParamListString()
        }

        // Super interfaces
        writeInheritanceList(ctInterface)

        val whereClause = typeParamHandler.generateWhereClause()
        if(whereClause.isNotEmpty()) {
            adapter write " where "
            adapter.writeAligned(whereClause)
        }

        adapter write SPACE and LEFT_CURL
        adapter.newline()
        adapter.pushIndent()

        for (it in ctInterface.typeMembers) {
            if(!it.isImplicit) { it.accept(this) }
        }

        adapter.popIndent()
        adapter.ensureNEmptyLines(0)
        adapter writeln RIGHT_CURL
    }

    private fun visitCtConstructor(ctConstructor: CtConstructor<*>, printDelegate: Boolean) {
        val isObject = ctConstructor.parent.getBooleanMetadata(KtMetadataKeys.CLASS_IS_OBJECT, false)
        val primary = ctConstructor.getMetadata(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY) as? Boolean? ?: false
        if (ctConstructor.isImplicit && !primary) return
        if(!isObject) {
            val modifierSet =
                getModifiersMetadata(ctConstructor)
                    .filterIf(ctConstructor.parent is CtEnum<*>) { it != KtModifierKind.PRIVATE }
            if (primary) {
                if (modifierSet.filterNot { it == KtModifierKind.PUBLIC }.isNotEmpty()
                    || ctConstructor.annotations.isNotEmpty()) {
                    adapter write SPACE
                    if(writeAnnotations(ctConstructor)) adapter.ensureSpaceOrNewlineBeforeNext()
                    adapter writeModifiers modifierSet.filterNot { it == KtModifierKind.PUBLIC }
                    adapter write "constructor"
                }
            } else {
                adapter.ensureNEmptyLines(1)
                if(writeAnnotations(ctConstructor)) adapter.ensureSpaceOrNewlineBeforeNext()
                adapter writeModifiers modifierSet and "constructor"
            }
            adapter write LEFT_ROUND
            visitCommaSeparatedList(ctConstructor.parameters)
            adapter write RIGHT_ROUND
        }
        if(ctConstructor.body != null) {
            visitConstructorBody(ctConstructor.body, primary, printDelegate)
        }
    }

    override fun <T : Any?> visitCtConstructor(ctConstructor : CtConstructor<T>) {
        visitCtConstructor(ctConstructor, true)
    }

    private fun visitConstructorBody(block: CtBlock<*>, primary: Boolean, printDelegate: Boolean) {
        enterCtStatement(block)
        if(block.statements.isEmpty()) return
        val first = block.statements[0]
        val rest: List<CtStatement>
        rest = if(first is CtConstructorCall<*>) {
            if(printDelegate && first.type.qualifiedName != "kotlin.Any") {
                visitCtConstructorCall(first)
            }
            block.statements.drop(1)
        } else {
            block.statements
        }

        if(!primary && rest.isNotEmpty()) {
            adapter write LEFT_CURL
            visitStatementList(rest)
            adapter.ensureNEmptyLines(0)
            adapter write RIGHT_CURL
        }
        exitCtStatement(block)
    }

    override fun visitCtPackageExport(p0: CtPackageExport?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtExecutableReference(p0: CtExecutableReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Enum<*>?> visitCtEnum(ctEnum: CtEnum<T>) {

        val modifiers = getModifiersMetadata(ctEnum)?.filterNot { it == KtModifierKind.OPEN }
        adapter writeModifiers modifiers

        adapter write "enum class" and SPACE and ctEnum.simpleName

        val primaryConstructor = ctEnum.constructors.firstOrNull { it.isPrimary() }
        if(primaryConstructor != null) {
            visitCtConstructor(primaryConstructor)
        }

        if(ctEnum.superInterfaces.isNotEmpty()) {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.OF_SUPERTYPE)
            var commas = ctEnum.superInterfaces.size - 1
            for (it in ctEnum.superInterfaces) {
                it.accept(this)
                val delegate = it.getMetadata(KtMetadataKeys.SUPER_TYPE_DELEGATE) as CtElement?
                if(delegate != null) {
                    adapter write " by "
                    delegate.accept(this)
                }
                if(commas-- > 0) adapter write ", "
            }
        }

        adapter write LEFT_CURL
        adapter.newline()
        adapter.pushIndent()

        //  ctClass.constructors.filterNot { it.isPrimary() }.forEach { it.accept(this) }
        if(ctEnum.enumValues.isNotEmpty()) {
            visitCommaSeparatedList(ctEnum.enumValues)
            adapter write ';'
        }

        ctEnum.typeMembers.filterNot { it is CtConstructor<*> && it.isPrimary() }.forEach {
            if(!it.isImplicit) { it.accept(this) }
        }
        adapter.popIndent()
        adapter.ensureNEmptyLines(0)
        adapter writeln RIGHT_CURL

    }

    private fun shouldAddPar(typeRef: CtTypeReference<*>): Boolean {
        val parentTypeAccess = typeRef.parent
        val parent = parentTypeAccess.parent
        if(parentTypeAccess is CtTypeAccess<*> && parent is CtMethod<*>) {
            val extensionRef = parent.getMetadata(KtMetadataKeys.EXTENSION_TYPE_REF)
            return extensionRef === parentTypeAccess
        }
        return false
    }

    private fun visitFunctionNType(typeRef: CtTypeReference<*>) {
        val numParams = typeRef.actualTypeArguments.size-1
        val params = typeRef.actualTypeArguments.subList(0, numParams)
        val returnType = typeRef.actualTypeArguments.last()
        val wrapped = shouldAddPar(typeRef)
        if(wrapped) {
            adapter write LEFT_ROUND
        }
        adapter write LEFT_ROUND
        visitCommaSeparatedList(params)
        adapter write RIGHT_ROUND and " -> "
        returnType.accept(this)
        if(wrapped) {
            adapter write RIGHT_ROUND
        }
    }

    override fun visitCtCompilationUnit(compilationUnit: CtCompilationUnit) {
        compilationUnit.packageDeclaration?.accept(this)
        adapter.ensureNEmptyLines(1)
        compilationUnit.imports.forEach { it.accept(this) }

        val pkg = compilationUnit.factory.Package().get(compilationUnit.packageDeclaration?.reference?.qualifiedName)
             ?: compilationUnit.factory.Package().rootPackage
        val topLvl = pkg.getType<CtType<*>>(topLvlClassName)
        val elements = ArrayList<CtElement>()
        elements.addAll(compilationUnit.declaredTypes)
        if(topLvl != null) {
            for(t in topLvl.typeMembers) {
                if(t.position.compilationUnit === compilationUnit) {
                    elements.add(t)
                }
            }
        }
        elements.sortBy { it.position.sourceStart }
        for(e in elements) {
            e.accept(this)
        }
        //compilationUnit.declaredTypes.forEach { it.accept(this) }
    }

    // Using CtNewArray as placeholder for string concat. CtNewArray is redundant for Kotlin and
    // this way we can skip introducing a new element for now
    override fun <T : Any?> visitCtNewArray(placeholder: CtNewArray<T>) {
        val args = placeholder.getMetadata(KtMetadataKeys.STRING_CONCAT_ELEMENTS) as List<CtElement>
        val multiLine = placeholder.getMetadata(KtMetadataKeys.STRING_LITERAL_MULTILINE) as Boolean? ?: false
        val quote = if(multiLine) "\"\"\"" else "\""
        val prevIndent = adapter.indentCount
        if(multiLine) adapter.indentCount = 0
        adapter write quote
        for(arg in args) {
            if(arg is CtLiteral<*> && arg.value is String) {
                if(multiLine) {
                    adapter write (arg.value as String)
                } else {
                    adapter write LiteralToStringHelper.getStringLiteral(arg.value as String)
                }
            } else {
                adapter write "\${"
                arg.accept(this)
                adapter write '}'
            }
        }
        adapter write quote
        if(multiLine) adapter.indentCount = prevIndent
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
        if(fieldRef.getBooleanMetadata(KtMetadataKeys.IS_ACTUAL_FIELD) == true) {
            adapter write "field"
        }
        else {
            adapter writeIdentifier fieldRef.simpleName
        }
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
        adapter writeIdentifier catchVariable.simpleName
        adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
        catchVariable.type.accept(this)
    }

    override fun <T : Any?, S : Any?> visitCtSwitchExpression(switch: CtSwitchExpression<T, S>) = visitWhen(switch)

    override fun visitCtTypeParameter(typeParam: CtTypeParameter) {
        val modifiers = typeParam.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>?
        val modifiersString = if(modifiers != null && modifiers.isNotEmpty()) {
            modifiers.toList().sorted().joinToString(separator = " ", postfix = " ", transform = { it.token })
        } else {
            ""
        }

        adapter write modifiersString
        adapter writeIdentifier typeParam.simpleName

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
        adapter writeIdentifier localVarRef.simpleName
    }

    override fun visitCtCodeSnippetStatement(p0: CtCodeSnippetStatement?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtArrayTypeReference(p0: CtArrayTypeReference<T>?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtCatchVariableReference(catchVarRef: CtCatchVariableReference<T>) {
        adapter writeIdentifier catchVarRef.simpleName
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

    override fun <A : Annotation?> visitCtAnnotation(ctAnnotation: CtAnnotation<A>?) {
        if(ctAnnotation == null) return
        adapter write '@'
        visitCtTypeReference(ctAnnotation.annotationType, false)
        if(ctAnnotation.values.isNotEmpty()) {
            adapter write LEFT_ROUND
            visitCommaSeparatedList(ctAnnotation.values.values.toList())
            adapter write RIGHT_ROUND
        }
    }

    override fun <T : Any?> visitCtThisAccess(thisAccess: CtThisAccess<T>) {
        if(thisAccess.isImplicit) return
        enterCtExpression(thisAccess)

        adapter write "this"
        val targetLabel = thisAccess.getMetadata(KtMetadataKeys.EXTENSION_THIS_TARGET) as String?
        if(targetLabel != null) {
            adapter write '@' and targetLabel
        }
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
        val multiLine = literal.getMetadata(KtMetadataKeys.STRING_LITERAL_MULTILINE) as Boolean? ?: false
        if(multiLine && literal.value is String) {
            adapter.withIndent(0) {
                it.write(LiteralToStringHelper.getLiteralToken(literal))
            }
        } else {
            adapter write (LiteralToStringHelper.getLiteralToken(literal))
        }

        exitCtExpression(literal)
    }

    override fun <T : Any?> visitCtBinaryOperator(binOp: CtBinaryOperator<T>) {
        enterCtExpression(binOp)

        val operator = binOp.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as? KtBinaryOperatorKind
        var token = operator?.asToken() ?: KtBinaryOperatorKind.fromJavaOperatorKind(binOp.kind).asToken()
        if(binOp.leftHandOperand == null || binOp.leftHandOperand.isImplicit) {
            token = token.dropWhile { it == ' ' }
        } else {
            binOp.leftHandOperand.accept(this)
        }
        adapter write token
        binOp.rightHandOperand.accept(this)

        exitCtExpression(binOp)
    }

    override fun <T : Any?> visitCtField(field: CtField<T>?) {
        if(field == null || field.isImplicit) return
        adapter.ensureNEmptyLines(0)

        // Annotations
        if(writeAnnotations(field)) adapter.ensureNEmptyLines(0)

        // Modifiers
        // Filter out redundant modifiers: 'open' if method has override modifier, and
        // 'abstract' and 'open' modifiers if method is member of an interface
        val modifierSet : Set<KtModifierKind>? = getModifiersMetadata(field)
        val modifiers = modifierSet?.filterIf(KtModifierKind.OVERRIDE in modifierSet) { it != KtModifierKind.OPEN }
            ?.filterIf(field.parent is CtInterface<*>) { it != KtModifierKind.ABSTRACT && it != KtModifierKind.OPEN }
            ?: setOf(KtModifierKind.VAR)

        adapter writeModifiers modifiers

        // Name
        adapter writeIdentifier field.simpleName

        // Type
        if(!field.type.isImplicit || forceExplicitTypes) {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
            visitCtTypeReference(field.type)
        }

        // Initializer or delegate
        adapter.pushIndent()
        visitDefaultExpr(field)


        val getter = field.getMetadata(KtMetadataKeys.PROPERTY_GETTER) as? CtMethod<*>?
        val setter = field.getMetadata(KtMetadataKeys.PROPERTY_SETTER) as? CtMethod<*>?
        if(getter != null) {
            adapter.ensureNEmptyLines(0)
            val getterModifiers = getModifiersMetadata(getter)
            adapter.writeModifiers(getterModifiers)
            adapter write "get() "
            if(getter.body.isImplicit) {
                adapter write "= "
            }
            visitCtBlock(getter.body)
        }
        if(setter != null) {
            adapter.ensureNEmptyLines(0)
            val setterModifiers = getModifiersMetadata(setter)
            adapter.writeModifiers(setterModifiers)
            adapter write "set" and LEFT_ROUND
            visitCommaSeparatedList(setter.parameters)
            adapter write RIGHT_ROUND
            if(setter.body.isImplicit) {
                adapter write "= "
            }
            visitCtBlock(setter.body)
        }

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
        adapter writeModifiers modifiers
        adapter writeIdentifier localVar.simpleName

        if(localVar.isImplicit) {
            if(localVar.getBooleanMetadata(KtMetadataKeys.IS_DESTRUCTURED) == true) {
                val components = localVar.getMetadata(KtMetadataKeys.COMPONENTS) as List<CtLocalVariable<*>>
                adapter write LEFT_ROUND
                visitCommaSeparatedList(components)
                adapter write RIGHT_ROUND
            }
        }
        else if(!localVar.isInferred || forceExplicitTypes) {
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

    override fun <S : Any?> visitCtCase(case: CtCase<S>) {
        if(case.caseExpressions.isEmpty()) {
            adapter write "else"
        } else {
            visitCommaSeparatedList(case.caseExpressions)
        }
        adapter write " -> "
        for(statement in case.statements) {
            statement.accept(this)
        }
    }

    override fun <T : Any?> visitCtCodeSnippetExpression(p0: CtCodeSnippetExpression<T>?) {
        TODO("Not yet implemented")
    }

    override fun visitCtUsedService(p0: CtUsedService?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, E : CtExpression<*>?> visitCtExecutableReferenceExpression(execRef: CtExecutableReferenceExpression<T, E>) {
        enterCtExpression(execRef)
        execRef.target!!.accept(this)
        
        adapter write "::"
        adapter writeIdentifier execRef.executable.simpleName
        exitCtExpression(execRef)
    }

    override fun visitCtProvidedService(p0: CtProvidedService?) {
        TODO("Not yet implemented")
    }

    override fun visitCtTypeParameterReference(typeParam: CtTypeParameterReference) {
        val name = TypeName.build(typeParam)
        if(typeParam.isSimplyQualified) {
            adapter writeIdentifier name.simpleNameWithNullability
        } else {
            adapter writeIdentifier name.fQNameWithNullability
        }
    }

    override fun visitCtModuleRequirement(p0: CtModuleRequirement?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtEnumValue(enumValue: CtEnumValue<T>) {
        when(val defaultExpr = enumValue.defaultExpression) {
            null -> { /* Nothing */}
            is CtNewClass<*> -> {
                adapter.ensureNEmptyLines(0)
                defaultExpr.accept(this)
            }
            else -> {
                if(!defaultExpr.isImplicit) {
                    adapter.ensureNEmptyLines(0)
                    adapter writeIdentifier enumValue.simpleName
                    adapter write LEFT_ROUND
                    visitCommaSeparatedList((defaultExpr as CtInvocation<*>).arguments)
                    adapter write RIGHT_ROUND
                }
            }
        }
    }

    override fun visitCtFor(p0: CtFor?) {
        TODO("Not yet implemented")
    }

    override fun visitCtSynchronized(p0: CtSynchronized?) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> visitCtParameter(param: CtParameter<T>) {
        if(param.isImplicit) {
            if(param.getBooleanMetadata(KtMetadataKeys.IS_DESTRUCTURED, false)) {
                val components = param.getMetadata(KtMetadataKeys.COMPONENTS) as List<CtLocalVariable<*>>
                adapter write LEFT_ROUND
                visitCommaSeparatedList(components) { adapter writeIdentifier it.simpleName }
                adapter write RIGHT_ROUND
            }
            return
        }

        if(writeAnnotations(param)) adapter.ensureSpaceOrNewlineBeforeNext()

        val modifierSet = getModifiersMetadata(param)
        adapter writeModifiers modifierSet
        adapter writeIdentifier param.simpleName
        if(!param.type.isImplicit) {
            adapter.writeColon(DefaultPrinterAdapter.ColonContext.DECLARATION_TYPE)
        }
        visitCtTypeReference(param.type)

        val defaultValue = param.getMetadata(KtMetadataKeys.PARAMETER_DEFAULT_VALUE) as? CtExpression<*>?
        if(defaultValue != null) {
            adapter write " = "
            defaultValue.accept(this)
        }
    }


    override fun <T : Any?> visitCtFieldWrite(fieldWrite: CtFieldWrite<T>) = visitFieldAccess(fieldWrite)

    override fun <T : Any?> visitCtFieldRead(fieldRead: CtFieldRead<T>) {
        if(fieldRead.getBooleanMetadata(KtMetadataKeys.IS_PROPERTY_REFERENCE, false)) {
            val parent = fieldRead.getParent(CtType::class.java)
            if(parent.qualifiedName == fieldRead.variable.declaringType.qualifiedName) {
                adapter write "this"
            } else {
                fieldRead.variable.declaringType.accept(this)
            }
            adapter write "::"
        }
        visitFieldAccess(fieldRead)
    }

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
        adapter writeIdentifier parameterRef.simpleName
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

        if(block.statements.isNotEmpty()) {
            visitStatementList(block.statements)
            adapter.ensureNEmptyLines(0)
        }
        adapter write RIGHT_CURL
        exitCtStatement(block)
    }

    private fun visitStatementList(statements: List<CtStatement>,
                           inlineSingleStatement: Boolean = false
    ) {
        if(statements.isEmpty()) return
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

    private fun shouldWriteTarget(newClass: CtNewClass<*>): Boolean {
        return newClass.parent is CtEnumValue<*> && newClass.anonymousClass.superclass?.simpleName ==
                newClass.parent.simpleName
    }

    override fun <T : Any?> visitCtNewClass(newClass: CtNewClass<T>) {
        if(newClass.getBooleanMetadata(KtMetadataKeys.CLASS_IS_OBJECT, false)) {
            visitObject(newClass.anonymousClass, true)
            return
        }
        if(shouldWriteTarget(newClass)) {
            newClass.type.accept(this)
        } else {
            adapter write newClass.type.simpleName
        }

        visitTypeArgumentsList(newClass.actualTypeArguments, false)
        visitArgumentList(newClass.arguments)

        adapter write SPACE and LEFT_CURL // TODO To generic function
        adapter.newline()
        adapter.pushIndent()

        newClass.anonymousClass.typeMembers.filterNot { it is CtConstructor<*> && it.isPrimary() }.forEach {
            if(!it.isImplicit) { it.accept(this) }
        }
        adapter.popIndent()
        adapter.ensureNEmptyLines(0)
        adapter write RIGHT_CURL
    }

    override fun <R : Any?> visitCtReturn(ctReturn: CtReturn<R>) {
        if(ctReturn.isImplicit && ctReturn.returnedExpression != null) { // FIXME Correct?
            ctReturn.returnedExpression.accept(this)
        } else {
            enterCtStatement(ctReturn)
            adapter write "return"
            val label = ctReturn.getMetadata(KtMetadataKeys.LABEL) as String?
            if(label != null) {
                adapter write '@' and label
            }
            if(ctReturn.returnedExpression != null) {
                adapter write SPACE
                ctReturn.returnedExpression.accept(this)
            }
            exitCtStatement(ctReturn)
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
                    visitCtTypeReference(typeRef, false)
                }

                override fun <T : Any?> visitUnresolvedImport(unresolvedImport: CtUnresolvedImport) {
                    adapter write unresolvedImport.unresolvedReference
                }

                override fun <T : Any?> visitMethodImport(execRef: CtExecutableReference<T>) {
                    val didWrite = visitCtTypeReference(execRef.declaringType, false)
                    if(didWrite) adapter write '.'
                    adapter writeIdentifier execRef.simpleName
                }

                override fun <T : Any?> visitFieldImport(fieldRef: CtFieldReference<T>) {
                    val didWrite = visitCtTypeReference(fieldRef.declaringType, false)
                    if(didWrite) adapter write '.'
                    adapter writeIdentifier fieldRef.simpleName
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

    override fun <S : Any?> visitCtSwitch(switch: CtSwitch<S>) = visitWhen(switch)

    private fun visitWhen(whenExpr: CtAbstractSwitch<*>) {
        adapter write "when" and SPACE
        if(whenExpr.selector != null) {
            adapter write LEFT_ROUND
            whenExpr.selector.accept(this)
            adapter write RIGHT_ROUND and SPACE
        }
        adapter write LEFT_CURL

        adapter.withIndentDiff(+1) {
            for(case in whenExpr.cases) {
                adapter.ensureNEmptyLines(0)
                case.accept(this)
            }
        }
        adapter.ensureNEmptyLines(0)
        adapter write RIGHT_CURL
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

    override fun <T : Any?> visitCtTypeReference(typeRef: CtTypeReference<T>) {
        if(typeRef.isImplicit) return

        visitCtTypeReference(typeRef, true)
    }

    private fun visitCtTypeReference(typeRef: CtTypeReference<*>, withGenerics: Boolean): Boolean {
        if(arrowSyntaxForFunctionType && typeRef.qualifiedName.matches("kotlin.Function[0-9]+".toRegex())) {
            visitFunctionNType(typeRef)
            return true
        }
        if(typeRef.simpleName == topLvlClassName) {
            if(typeRef.declaringType == null) {
                if(typeRef.`package` == null) return false
                visitCtPackageReference(typeRef.`package`)
                return !typeRef.`package`.isUnnamedPackage
            }
            return visitCtTypeReference(typeRef.declaringType, false)
        }

        if(typeRef.declaringType != null) {
            visitCtTypeReference(typeRef.declaringType, false)
            adapter write '.'
        } else if (typeRef.`package` != null && !typeRef.`package`.isUnnamedPackage) {
            visitCtPackageReference(typeRef.`package`)
            adapter write '.'
        }
        val name = TypeName.build(typeRef)
        adapter writeIdentifier name.simpleNameWithoutNullability

        if(withGenerics)
            visitTypeArgumentsList(typeRef.actualTypeArguments, false)

        adapter write name.suffix
        return true
    }

    override fun <T : Any?> visitCtVariableWrite(varWrite: CtVariableWrite<T>) {
        adapter writeIdentifier varWrite.variable.simpleName
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
        enterCtExpression(arrayAccess)
        arrayAccess.target.accept(this)
        adapter write LEFT_SQUARE
        visitCommaSeparatedList(arrayAccess.getMetadata(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS) as List<CtElement>)
        adapter write RIGHT_SQUARE
        exitCtExpression(arrayAccess)
    }

    override fun <T : Any?> visitCtInvocation(invocation: CtInvocation<T>?) {
        if(invocation == null || invocation.isImplicit) return
        if(invocation.getMetadata(KtMetadataKeys.INVOCATION_IS_INFIX) as Boolean? == true) {
            return visitInfixInvocation(invocation)
        }
        enterCtStatement(invocation)
        enterCtExpression(invocation)

        var separator = ""
        if(invocation.target != null && !invocation.target.isImplicit) {
            invocation.target.accept(this)
            val isSafe = invocation.getMetadata(KtMetadataKeys.ACCESS_IS_SAFE) as Boolean?
            separator = if(isSafe == true) "?." else "."
        }

        if(!shouldIgnoreIdentifier(invocation)) { // If invoke operator, the name of the called function is omitted
            adapter write separator
            adapter writeIdentifier invocation.executable.simpleName
        } else if(invocation.executable.isConstructor) {
            adapter write separator
            val declaringType = invocation.executable.declaringType?.declaration
            val modifiers = declaringType?.getMetadata(KtMetadataKeys.KT_MODIFIERS) as Set<KtModifierKind>?
            if(modifiers != null && KtModifierKind.INNER in modifiers) {
                adapter writeIdentifier invocation.executable.type.simpleName
            } else {
                visitCtTypeReference(invocation.executable.type)
            }
        }

        if(!invocation.executable.isConstructor) {
            visitTypeArgumentsList(invocation.actualTypeArguments, false)
        }
        
        visitArgumentList(invocation.arguments) // Paren handled in call

        exitCtExpression(invocation)
        exitCtStatement(invocation)
    }

    private fun visitTypeArgumentsList(typeArguments: List<CtTypeReference<*>>, forceExplicitTypeArgs: Boolean) {
        if(typeArguments.isNotEmpty() &&
            (forceExplicitTypeArgs || forceExplicitTypes || typeArguments.any { !it.isImplicit })) {
            adapter write LEFT_ANGLE
            visitCommaSeparatedList(typeArguments)
            adapter write RIGHT_ANGLE
        }
    }

    private fun shouldIgnoreIdentifier(invocation: CtInvocation<*>): Boolean {
        if(invocation.executable.simpleName == "invoke") {
            return invocation.getBooleanMetadata(KtMetadataKeys.INVOKE_AS_OPERATOR, false)
        }
        return invocation.executable?.isConstructor ?: false
    }

    private fun <T> visitInfixInvocation(ctInvocation: CtInvocation<T>) {
        enterCtExpression(ctInvocation)
        ctInvocation.target.accept(this)
        adapter write SPACE
        adapter writeIdentifier ctInvocation.executable.simpleName and SPACE
        ctInvocation.arguments[0].accept(this)
        exitCtExpression(ctInvocation)
    }

    private fun writeAnnotations(ctElement: CtElement): Boolean {
        if(ctElement.annotations.isEmpty()) return false
        for(a in ctElement.annotations) {
            adapter.ensureNEmptyLines(0)
            visitCtAnnotation(a)
        }
        return true
    }

    override fun <T : Any?> visitCtMethod(method: CtMethod<T>) {
        if(method.isImplicit) return
        // Annotations not implemented
        adapter.ensureNEmptyLines(1)
        writeAnnotations(method)
        adapter.ensureNEmptyLines(0)
        // Modifiers
        val modifierSet = getModifiersMetadata(method)
        // Filter out redundant modifiers: 'open' if method has override modifier, and
        // 'abstract' and 'open' modifiers if method is member of an interface
        val modifiers = modifierSet.filterIf(KtModifierKind.OVERRIDE in modifierSet) { it != KtModifierKind.OPEN }
            .filterIf(method.parent is CtInterface<*>) { it != KtModifierKind.ABSTRACT && it != KtModifierKind.OPEN }

        adapter writeModifiers modifiers and "fun"

        val typeParamHandler = TypeParameterHandler(method, this, false)
        adapter write typeParamHandler.generateTypeParamListString() and SPACE

        val extensionTypeRef = method.getMetadata(KtMetadataKeys.EXTENSION_TYPE_REF) as CtTypeAccess<*>?
        if(extensionTypeRef != null) {
            extensionTypeRef.accept(this)
            adapter write '.'
        }

        adapter writeIdentifier method.simpleName and LEFT_ROUND
        visitCommaSeparatedList(method.parameters)
        adapter write RIGHT_ROUND

        if(!method.type.isImplicit && method.type.qualifiedName != "kotlin.Unit") {
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