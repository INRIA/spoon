package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.impl.FirDefaultPropertyGetter
import org.jetbrains.kotlin.fir.declarations.impl.FirDefaultPropertySetter
import org.jetbrains.kotlin.fir.declarations.impl.FirPrimaryConstructorImpl
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirSingleExpressionBlock
import org.jetbrains.kotlin.fir.expressions.impl.FirUnitExpression
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.FirBackingFieldReference
import org.jetbrains.kotlin.fir.references.FirErrorNamedReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirSuperReference
import org.jetbrains.kotlin.fir.references.impl.FirExplicitThisReference
import org.jetbrains.kotlin.fir.references.impl.FirImplicitThisReference
import org.jetbrains.kotlin.fir.references.impl.FirPropertyFromParameterResolvedNamedReference
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeProjection
import org.jetbrains.kotlin.fir.types.isNullableAny
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import spoon.SpoonException
import spoon.kotlin.compiler.*
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.KtStatementExpressionImpl
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.*
import spoon.support.reflect.code.CtLiteralImpl
import kotlin.collections.ArrayList

class FirTreeBuilder(val factory : Factory,
                     val session: FirSession,
                     val delegateMap: Map<ClassId, MutableMap<ClassId, FirStatement>>,
                     spoonKtEnvironment: SpoonKtEnvironment
) : FirVisitor<CompositeTransformResult<CtElement>, ContextData?>() {
    internal val referenceBuilder = ReferenceBuilder(this)
    internal val helper = FirTreeBuilderHelper(this, spoonKtEnvironment)
    internal val toplvlClassName = "<top-level>"


    override fun visitElement(element: FirElement, data: ContextData?): CompositeTransformResult<CtElement> {
        //throw SpoonException("Element type not implemented $element")
        if(element is FirUnitExpression) return CompositeTransformResult.empty()
        return CtLiteralImpl<String>().setValue<CtLiteral<String>>("Unimplemented element $element").compose()
    }

    override fun visitErrorNamedReference(
        errorNamedReference: FirErrorNamedReference,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        throw RuntimeException("Error, file contains compile errors: ${errorNamedReference.diagnostic.reason}")
    }

    fun addModifiersAsMetadata(element: CtElement, modifierList: List<KtModifierKind>) {
        element.putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toMutableSet())
    }

    override fun visitFile(file: FirFile, data: ContextData?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(file.session, factory)
        val compilationUnit = factory.CompilationUnit().getOrCreate(file.name)

        val pkg = if(file.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(file.packageFqName.asString(), module)

        compilationUnit.declaredPackage = pkg

        val ktFile = file.psi
        if(ktFile is KtFile) {
            compilationUnit.lineSeparatorPositions = with(ktFile) {
                if (hasNormalizedEOL()) lineSeparatorPositions() else lineSeparatorPositionsRaw()
            }
        }

        val transformedTopLvlDecl = file.declarations.map {
            it.accept(this, null)
        }
        for(it in transformedTopLvlDecl) {
            val t = it.single
            t.setParent(compilationUnit)
            when(t) {
                is CtType<*> -> {
                    pkg.addType<CtPackage>(t)
                    compilationUnit.addDeclaredType(t)
                }
                is CtTypeMember -> { // Top level function or property declaration
                    val topLvl = pkg.getType<CtType<Any>>(toplvlClassName) ?: (factory.Core().createClass<Any>().also {
                            topLvlClass ->
                            topLvlClass.setImplicit<CtClass<*>>(true)
                            topLvlClass.setSimpleName<CtClass<*>>(toplvlClassName)
                            pkg.addType<CtPackage>(topLvlClass)
                            compilationUnit.addDeclaredType(topLvlClass)
                        })
                    topLvl.addTypeMember<CtClass<Any>>(t)
                }
            }
        }

        return compilationUnit.compose()
    }

    override fun visitRegularClass(regularClass: FirRegularClass, data: ContextData?): CompositeTransformResult.Single<CtType<*>> {
        val module = helper.getOrCreateModule(regularClass.session, factory)
        val type = helper.createType(regularClass)
        val isObject = type.getMetadata(KtMetadataKeys.CLASS_IS_OBJECT) as Boolean? == true

        if(regularClass.classId.isLocal) {
            return type.compose()
        }
        if(!regularClass.classId.isNestedClass) {
            val pkg = if (regularClass.classId.packageFqName.isRoot) module.rootPackage else
                  factory.Package().getOrCreate(regularClass.classId.packageFqName.asString(), module)
            pkg.addType<CtPackage>(type)
        }

        // Modifiers
        val modifierList = KtModifierKind.fromClass(regularClass)
        addModifiersAsMetadata(type, modifierList)

        // Type parameters
        if(regularClass.typeParameters.isNotEmpty()) {
            type.setFormalCtTypeParameters<CtType<*>>(
                regularClass.typeParameters.map { visitTypeParameter(it,null).single })
        }

        for(decl in regularClass.declarations) {
            val ctDecl = decl.accept(this, null).single
            ctDecl.setParent(type)
            when (ctDecl) {
                is CtEnumValue<*> -> {
                    (type as CtEnum<Enum<*>>).addEnumValue<CtEnum<Enum<*>>>(ctDecl)
                }
                is CtField<*> -> type.addField(ctDecl)
                is CtMethod<*> -> {
                    if (regularClass.isInterface() && ctDecl.body != null) {
                        ctDecl.setDefaultMethod<Nothing>(true)
                    }
                    if(decl.psi is KtClass) {
                        ctDecl.setImplicit<CtMethod<*>>(true)
                    }
                    type.addMethod(ctDecl)
                }
                is CtConstructor<*> -> {
                    if (type is CtClass<*> && !isObject) {
                        (type as CtClass<Any>).addConstructor<CtClass<Any>>(ctDecl as CtConstructor<Any>)
                    }
                }
                is CtTypeMember -> {
                    type.addTypeMember(ctDecl)
                }

            }
        }
        return type.compose()
    }

    private fun transformAndAddTypeMembers(ctType: CtType<*>, declarations: List<FirDeclaration>) {
        val isObject = ctType.getMetadata(KtMetadataKeys.CLASS_IS_OBJECT) as Boolean? == true
        for(decl in declarations) {
            val ctDecl = decl.accept(this, null).single
            ctDecl.setParent(ctType)
            when (ctDecl) {
                is CtField<*> -> ctType.addField(ctDecl)
                is CtMethod<*> -> {
                    if (ctDecl.body != null) {
                        ctDecl.setDefaultMethod<Nothing>(true)
                    }
                    ctType.addMethod(ctDecl)
                }
                is CtConstructor<*> -> {
                    if (ctType is CtClass<*>) {
                        (ctType as CtClass<Any>).addConstructor<CtClass<Any>>(ctDecl as CtConstructor<Any>)
                        if(isObject) ctDecl.setImplicit<CtConstructor<*>>(true)
                    }
                }
                is CtTypeMember -> {
                    ctType.addTypeMember(ctDecl)
                }
            }
        }
    }

    override fun visitEnumEntry(enumEntry: FirEnumEntry, data: ContextData?): CompositeTransformResult.Single<CtEnumValue<*>> {
        val ctEnum = factory.Core().createEnumValue<Any>()
        ctEnum.setSimpleName<CtEnumValue<*>>(enumEntry.name.identifier)
        val constr = enumEntry.declarations.firstIsInstance<FirPrimaryConstructorImpl>()
        val enumTypeRef = referenceBuilder.getNewTypeReference<Any>(constr.delegatedConstructor!!.constructedTypeRef)
        val delegate = visitDelegatedConstructorCall(constr.delegatedConstructor!!, null).single as CtConstructorCall<Any>
        if(enumEntry.declarations.size == 1) {
            ctEnum.setType<CtEnumValue<*>>(enumTypeRef)
            ctEnum.setDefaultExpression<CtEnumValue<Any>>(delegate)
        } else {
            val anonClass = factory.Core().createNewClass<Any>()
            anonClass.setAnonymousClass<CtNewClass<*>>(createAnonymousClass(enumEntry).single)
            anonClass.setArguments<CtNewClass<Any>>(delegate.arguments)
            anonClass.setExecutable<CtNewClass<Any>>(referenceBuilder.getNewExecutableReference(constr.delegatedConstructor!!, constr.returnTypeRef))
            ctEnum.setDefaultExpression<CtVariable<Any>>(anonClass)
        }
        return ctEnum.compose()

    }

    override fun visitDelegatedConstructorCall(
        delegatedConstructorCall: FirDelegatedConstructorCall,
        data: ContextData?
    ): CompositeTransformResult.Single<CtConstructorCall<*>> {
        val ctConstructorCall = factory.Core().createConstructorCall<Any>()
        ctConstructorCall.setExecutable<CtConstructorCall<Any>>(referenceBuilder.getNewExecutableReference<Any>(delegatedConstructorCall,
            delegatedConstructorCall.constructedTypeRef))
        ctConstructorCall.setArguments<CtConstructorCall<Any>>(delegatedConstructorCall.arguments.map {
            expressionOrWrappedInStatementExpression(it.accept(this,null).single)
        })
        return ctConstructorCall.compose()
    }

    private fun createAnonymousClass(firEnumEntry: FirEnumEntry): CompositeTransformResult.Single<CtClass<*>> {
        val ctClass = factory.Core().createClass<Any>()
        ctClass.setSimpleName<CtType<*>>(firEnumEntry.name.identifier)
        transformAndAddTypeMembers(ctClass, firEnumEntry.declarations)
        return ctClass.compose()
    }

    override fun visitConstructor(constructor: FirConstructor, data: ContextData?): CompositeTransformResult.Single<CtConstructor<*>> {
        val ctConstructor = factory.Core().createConstructor<Any>()
        ctConstructor.setSimpleName<CtConstructor<*>>(constructor.name.asString())

        val modifierList = listOfNotNull(KtModifierKind.convertVisibility(constructor.visibility))
        ctConstructor.setImplicit<CtConstructor<Any>>(constructor.isPrimary &&
                constructor.valueParameters.isEmpty() &&
                constructor.body == null &&
                modifierList.filterNot { it == KtModifierKind.PUBLIC }.isEmpty() &&
                constructor.source?.psi !is KtPrimaryConstructor
        )

        ctConstructor.putMetadata<CtConstructor<*>>(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY, constructor.isPrimary)

        addModifiersAsMetadata(ctConstructor, modifierList)

        // Add body
        val body = constructor.body?.accept(this, null)?.single as? CtStatement?
        if(body != null) {
            ctConstructor.setBody<CtConstructor<*>>(body)
        }

        // Add params
        constructor.valueParameters.forEach {
            val p = it.accept(this,null).single as CtParameter<*>
            /*
            * Primary constructor property declaration creates implicit properties in the class. An implicit property is the
            * holder of the val/var modifier, not the parameter:
            * ClassName(var x = 2) >translates to> ClassName(x = 2) { var x = x }
            * To facilitate printing, we look in the PSI if the parameter has modifiers and add them to metadata.
            *
            * TODO: Perhaps add metadata mapping property <-> param?
            *  */
            if(constructor.isPrimary) {
                val pModifiers = (p.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? MutableSet<KtModifierKind>?) ?:
                        mutableSetOf<KtModifierKind>()

                val psiModifiersList = it.source.psi?.getChildrenOfType<KtModifierList>()?.let { lists ->
                    if(lists.isNotEmpty()) { KtModifierKind.fromPsiModifierList(lists[0]) }
                    else emptyList()
                } ?: emptyList()
                pModifiers.addAll(psiModifiersList)
                // Var/val might be outside of modifier list
                val psiTokens = it.source.psi?.getChildrenOfType<LeafPsiElement>()
                if(psiTokens?.any { t -> t.elementType == KtTokens.VAL_KEYWORD } == true) {
                    pModifiers.add(KtModifierKind.VAL)
                } else if(psiTokens?.any { t -> t.elementType == KtTokens.VAR_KEYWORD } == true) {
                    pModifiers.add(KtModifierKind.VAR)
                }
                p.putMetadata<CtParameter<*>>(KtMetadataKeys.KT_MODIFIERS, pModifiers)
            }
            ctConstructor.addParameter(p) // Sets parent
        }

        // Add delegate call
        val delegatedConstr = constructor.delegatedConstructor
        if(delegatedConstr != null) {
            val invocation = factory.Core().createInvocation<Any>()
           // invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(delegatedConstr,
           // constructor.returnTypeRef))
            invocation.setExecutable<CtInvocation<Any>>(
                ConstructorDelegateResolver.resolveSuperCallBug(
                this, constructor)) // Sets parent

            delegatedConstr.arguments.forEach {
                val arg = it.accept(this,null).single as CtExpression<*>
                invocation.addArgument(arg) // Sets parent
            }
            ctConstructor.putMetadata<CtConstructor<*>>(KtMetadataKeys.CONSTRUCTOR_DELEGATE_CALL, invocation)
            invocation.setParent(ctConstructor)

            if (invocation.executable.type.simpleName == "Any") {
                invocation.setImplicit<CtInvocation<Any>>(true)
                invocation.executable.setImplicit<CtExecutableReference<Any>>(true)
            }
        }

        return ctConstructor.compose()
    }

    private fun getReceiver(qa: FirQualifiedAccessExpression): CtElement? {
        val explicitReceiver = qa.explicitReceiver
        val dispatchReceiver = qa.dispatchReceiver
        val receiver = if(explicitReceiver == null || explicitReceiver == FirNoReceiverExpression) {
            if(dispatchReceiver == FirNoReceiverExpression) null
            else dispatchReceiver.accept(this,null)
        } else {
            explicitReceiver.accept(this,null)
        }
        if(receiver != null && receiver.isSingle) return receiver.single
        return null
    }

    override fun visitTypeOperatorCall(
        typeOperatorCall: FirTypeOperatorCall,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        return when(typeOperatorCall.operation) {
            FirOperation.IS, FirOperation.NOT_IS -> visitIsTypeOperation(typeOperatorCall)
            FirOperation.AS, FirOperation.SAFE_AS -> visitTypeCast(typeOperatorCall)
            else -> throw SpoonException("${typeOperatorCall.operation} is not a type operator")
        }
    }

    override fun visitResolvedTypeRef(
        resolvedTypeRef: FirResolvedTypeRef,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val typeAccess = factory.Core().createTypeAccess<Any>()
        typeAccess.setAccessedType<CtTypeAccess<Any>>(referenceBuilder.getNewTypeReference(resolvedTypeRef))
        return typeAccess.compose()
    }

    override fun visitAnonymousInitializer(
        anonymousInitializer: FirAnonymousInitializer,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val ctAnonExec = factory.Core().createAnonymousExecutable()
        val body = anonymousInitializer.body?.accept(this,null)?.single as CtStatement?
        ctAnonExec.setBody<CtAnonymousExecutable>(body)
        return ctAnonExec.compose()
    }

    private fun visitIsTypeOperation(typeOperatorCall: FirTypeOperatorCall):
            CompositeTransformResult.Single<CtBinaryOperator<*>> {
        val ctBinaryOperator = factory.Core().createBinaryOperator<Boolean>()
        ctBinaryOperator.putMetadata<CtBinaryOperator<*>>(
            KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtBinaryOperatorKind.fromFirOperation(typeOperatorCall.operation))
        ctBinaryOperator.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(typeOperatorCall.typeRef))
        val lhs = typeOperatorCall.argument.accept(this,null).single as CtExpression<*>
        val rhs = typeOperatorCall.conversionTypeRef.accept(this,null).single as CtExpression<*>
        ctBinaryOperator.setLeftHandOperand<CtBinaryOperator<Boolean>>(lhs)
        ctBinaryOperator.setRightHandOperand<CtBinaryOperator<Boolean>>(rhs)
        return ctBinaryOperator.compose()
    }

    private fun visitTypeCast(typeOperatorCall: FirTypeOperatorCall): CompositeTransformResult.Single<CtExpression<*>> {
        val castedExpr = typeOperatorCall.argument.accept(this,null).single as CtExpression<Any>
        val conversionTypeRef = referenceBuilder.getNewTypeReference<Any>(typeOperatorCall.conversionTypeRef)
        castedExpr.addTypeCast<CtExpression<Any>>(conversionTypeRef)

        val safe = typeOperatorCall.operation == FirOperation.SAFE_AS
        conversionTypeRef.putMetadata<CtTypeReference<*>>(KtMetadataKeys.TYPE_CAST_AS_SAFE, safe)
        return castedExpr.compose()
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: ContextData?): CompositeTransformResult<CtElement> {
        val invocationType = helper.resolveIfOperatorOrInvocation(functionCall)
        return when(invocationType) {
            is InvocationType.NORMAL_CALL -> visitNormalFunctionCall(invocationType, data)
            is InvocationType.INFIX_CALL -> visitInfixFunctionCall(invocationType)
            is InvocationType.BINARY_OPERATOR -> visitBinaryOperatorViaFunctionCall(invocationType)
            is InvocationType.ASSIGNMENT_OPERATOR -> visitAssignmentOperatorViaFunctionCall(invocationType)
            is InvocationType.POSTFIX_OPERATOR -> TODO()
            is InvocationType.PREFIX_OPERATOR -> visitUnaryOperatorViaFunctionCall(invocationType)
            is InvocationType.GET_OPERATOR -> visitGetOperator(invocationType)
            is InvocationType.SET_OPERATOR -> visitSetOperator(invocationType)
            is InvocationType.UNKNOWN -> throw RuntimeException("Unknown invocation type ${functionCall.calleeReference.name.asString()}")
        }
    }

    private fun visitGetOperator(invocationType: InvocationType.GET_OPERATOR): CompositeTransformResult.Single<CtArrayRead<*>> {
        // Temporarily use CtArrayAccess, will prob need its own class (or make normal call with is operator call in metadata)
        val receiver = invocationType.receiver.accept(this,null).single as CtExpression<*>
        val ctArrAccess = factory.Core().createArrayRead<Any>()
        ctArrAccess.setTarget<CtArrayRead<Any>>(receiver)
        ctArrAccess.setType<CtArrayRead<Any>>(referenceBuilder.getNewTypeReference(invocationType.originalFunction.typeRef))
        val args = invocationType.args.map { it.accept(this,null).single.apply { setParent(ctArrAccess) } }
        ctArrAccess.putMetadata<CtArrayRead<*>>(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS, args)
        return ctArrAccess.compose()
    }

    private fun visitSetOperator(invocationType: InvocationType.SET_OPERATOR): CompositeTransformResult.Single<CtAssignment<*,*>>  {
        // Temporarily use CtArrayAccess, will prob need its own class (or make normal call with is_operator_call in metadata)
        val receiver = invocationType.receiver.accept(this,null).single as CtExpression<*>
        val rhs = invocationType.rhs.accept(this,null).single as CtExpression<Any>
        val ctAssignment = factory.Core().createAssignment<Any, Any>()
        val ctArrayWrite = factory.Core().createArrayWrite<Any>()
        val args = invocationType.args.map { it.accept(this,null).single.apply { setParent(ctArrayWrite) } }
        ctArrayWrite.setTarget<CtArrayWrite<Any>>(receiver)
        ctArrayWrite.setType<CtArrayWrite<Any>>(referenceBuilder.getNewTypeReference(invocationType.originalFunction.typeRef))
        ctArrayWrite.putMetadata<CtArrayWrite<*>>(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS, args)
        ctAssignment.setAssigned<CtAssignment<Any,Any>>(ctArrayWrite)
        ctAssignment.setAssignment<CtAssignment<Any,Any>>(rhs)
        return ctAssignment.compose()
    }

    override fun visitTypeProjection(
        typeProjection: FirTypeProjection,
        data: ContextData?
    ): CompositeTransformResult.Single<CtTypeReference<*>> {
        return referenceBuilder.visitTypeProjection(typeProjection).compose()
    }

    private fun visitNormalFunctionCall(call: InvocationType.NORMAL_CALL, context: ContextData?):
            CompositeTransformResult<CtInvocation<*>> {
        val (firReceiver, functionCall) = call
        val invocation = factory.Core().createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(functionCall))

        val nonSpecialTarget = firReceiver?.accept(this,null)
        val target: CtElement?
        target = if(nonSpecialTarget?.isEmpty == true) {
            if(context is Destruct) {
                context.destructTarget.accept(this,null).single
            } else {
                return CompositeTransformResult.empty()
            }
        } else {
            nonSpecialTarget?.single
        }
        if(target is CtExpression<*>) {
            invocation.setTarget<CtInvocation<Any>>(target)
        } else if(target != null) {
            throw RuntimeException("Function call target not CtExpression")
        }

        if(functionCall.arguments.isNotEmpty()) {
            invocation.setArguments<CtInvocation<Any>>(functionCall.arguments.map {
                expressionOrWrappedInStatementExpression(it.accept(this,null).single)
            })
        }

        if(functionCall.typeArguments.isNotEmpty()) {
            invocation.setActualTypeArguments<CtInvocation<*>>(
                functionCall.typeArguments.map { visitTypeProjection(it, null).single }
            )
        }

        invocation.putMetadata<CtInvocation<*>>(KtMetadataKeys.ACCESS_IS_SAFE, functionCall.safe)

        // Handle special invoke operator
        if(helper.resolveIfInvokeOperatorCall(functionCall) == true) {
            invocation.putMetadata<CtInvocation<*>>(KtMetadataKeys.INVOKE_AS_OPERATOR, true)
        }

        return invocation.compose()
    }

    private fun visitAssignmentOperatorViaFunctionCall(assignmentType: InvocationType.ASSIGNMENT_OPERATOR):
            CompositeTransformResult.Single<CtOperatorAssignment<*,*>> {
        val (firLhs, kind, firRhs, opFunc) = assignmentType
        val ctAssignmentOp = factory.createOperatorAssignment<Any,Any>()
        ctAssignmentOp.setKind<CtOperatorAssignment<Any,Any>>(kind.toJavaAssignmentOperatorKind())
        val lhs = expressionOrWrappedInStatementExpression(firLhs.accept(this,null).single)
        val rhs = expressionOrWrappedInStatementExpression(firRhs.accept(this,null).single)
        ctAssignmentOp.setAssigned<CtOperatorAssignment<Any,Any>>(lhs as CtExpression<Any>)
        ctAssignmentOp.setAssignment<CtOperatorAssignment<Any,Any>>(rhs as CtExpression<Any>)
        ctAssignmentOp.setType<CtOperatorAssignment<Any,Any>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))
        return ctAssignmentOp.compose()
    }

    private fun visitInfixFunctionCall(invocationType: InvocationType.INFIX_CALL):
        CompositeTransformResult.Single<CtInvocation<*>> {
        val (firLhs, originalFunc, firRhs) = invocationType
        val invocation = factory.Core().createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(originalFunc))
        val lhs = firLhs.accept(this,null).single as CtExpression<*>
        val rhs = expressionOrWrappedInStatementExpression(firRhs.accept(this,null).single)
        invocation.setTarget<CtInvocation<Any>>(lhs)
        invocation.setArguments<CtInvocation<Any>>(listOf(rhs))
        invocation.putMetadata<CtInvocation<Any>>(KtMetadataKeys.INVOCATION_IS_INFIX, true)
        return invocation.compose()
    }

    private fun visitBinaryOperatorViaFunctionCall(binType: InvocationType.BINARY_OPERATOR):
            CompositeTransformResult.Single<CtBinaryOperator<*>> {
        val (firLhs, kind, firRhs) = binType
        val opFunc = binType.originalFunction
        val ktOp = factory.Core().createBinaryOperator<Any>()
        val lhs = firLhs.accept(this,null).single
        val rhs = firRhs.accept(this,null).single
        ktOp.setLeftHandOperand<CtBinaryOperator<Any>>(lhs as CtExpression<*>?)
        ktOp.setRightHandOperand<CtBinaryOperator<Any>>(rhs as CtExpression<*>)
        ktOp.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))

        ktOp.putMetadata<CtBinaryOperator<Any>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, kind)
        return ktOp.compose()
    }

    private fun visitUnaryOperatorViaFunctionCall(invocationType: InvocationType.PREFIX_OPERATOR):
            CompositeTransformResult.Single<CtUnaryOperator<*>> {
        val (kind, operand, opFunc) = invocationType
        val ctOp = factory.Core().createUnaryOperator<Any>()
        val ctOperand = operand.accept(this,null).single as CtExpression<Any>
        ctOp.setOperand<CtUnaryOperator<*>>(ctOperand)
        ctOp.setKind<CtUnaryOperator<*>>(kind)
        ctOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))
        return ctOp.compose()
    }

    override fun visitExpressionWithSmartcast(
        expressionWithSmartcast: FirExpressionWithSmartcast,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        if(expressionWithSmartcast.originalExpression.source?.psi is KtUnaryExpression) return CompositeTransformResult.empty()
        val expr = expressionWithSmartcast.originalExpression.accept(this,null).single as CtTypedElement<*>
        val smartType = referenceBuilder.getNewTypeReference<Any>(expressionWithSmartcast.typeRef)
        if(expr.type != smartType)
            (expr as CtTypedElement<Any>).setType<CtTypedElement<Any>>(smartType)

        return expr.compose()
    }

    override fun visitNamedArgumentExpression(
        namedArgumentExpression: FirNamedArgumentExpression,
        data: ContextData?
    ): CompositeTransformResult.Single<CtElement> {
        return namedArgumentExpression.expression.accept(this,null).single.apply {
            putMetadata<CtElement>(KtMetadataKeys.NAMED_ARGUMENT, namedArgumentExpression.name.asString())
        }.compose()
    }

    override fun visitWhenExpression(whenExpression: FirWhenExpression, data: ContextData?): CompositeTransformResult<CtElement> {
        if(whenExpression.isIf()) return visitIfExpression(whenExpression)
        val subjectVariable = whenExpression.subjectVariable
        if(subjectVariable?.name?.isSpecial == true) {
            if(subjectVariable.name.asString() == "<elvis>")
                return visitElvisOperator(whenExpression)
            else throw SpoonException(
                "Unexpected special in subject variable of when-expression: ${subjectVariable.name.asString()}")
        }

        val ctSwitch = if(helper.whenIsStatement(whenExpression)) {
            factory.Core().createSwitch<Any>()
        } else {
            factory.Core().createSwitchExpression<Any,Any>()
        }
        val subject = subjectVariable?.accept(this,null)?.single ?:
            whenExpression.subject?.accept(this, null)?.single
        if(subject != null) {
            ctSwitch.setSelector<CtAbstractSwitch<Any>>(expressionOrWrappedInStatementExpression(subject))
        }
        ctSwitch.setCases<CtAbstractSwitch<Any>>(whenExpression.branches.map { visitWhenBranch(it, null).single })

        return ctSwitch.compose()
    }

    override fun visitWhenBranch(whenBranch: FirWhenBranch, data: ContextData?): CompositeTransformResult.Single<CtCase<Any>> {
        val case = factory.Core().createCase<Any>()
        case.setCaseKind<CtCase<Any>>(CaseKind.ARROW)

        fun markImplicitLHS(expr: CtElement) {
            when(expr) {
                is CtBinaryOperator<*> -> {
                    when(expr.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind?) {
                        KtBinaryOperatorKind.IS,
                        KtBinaryOperatorKind.IS_NOT,
                        KtBinaryOperatorKind.IN,
                        KtBinaryOperatorKind.NOT_IN -> expr.leftHandOperand.setImplicit<CtBinaryOperator<*>>(true)
                        else -> { /* Nothing */ }
                    }
                }
            }
        }

        case.setCaseExpressions<CtCase<Any>>(helper.resolveWhenBranchMultiCondition(whenBranch).map {
            it.first.accept(this,null).single.also { expr ->
                expr.setParent(case)
                if(it.second) markImplicitLHS(expr)
            } as CtExpression<Any>
        })

        val result = visitBlock(whenBranch.result, null).single
        case.addStatement<CtCase<*>>(result)
        return case.compose()
    }

    override fun visitWhenSubjectExpression(
        whenSubjectExpression: FirWhenSubjectExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val subjectVariable = whenSubjectExpression.whenSubject.whenExpression.subjectVariable
        if(subjectVariable != null) {
            val read = factory.Core().createVariableRead<Any>()
            read.setVariable<CtVariableRead<Any>>(referenceBuilder.getNewVariableReference<Any>(subjectVariable))
            return read.compose()
        }
        // Subject has wrong type in typearg for its typeref, but it can be found in the actual type arg instead
        val subject = whenSubjectExpression.whenSubject.whenExpression.subject
        return subject?.accept(this,null) ?: CompositeTransformResult.empty()
    }

    private fun visitElvisOperator(whenExpression: FirWhenExpression): CompositeTransformResult<CtBinaryOperator<*>> {
        val lhs = whenExpression.subjectVariable!!.initializer!!.accept(this,null).single as CtExpression<*>
        val rhs = whenExpression.branches.first { it.condition !is FirElseIfTrueCondition }.
            result.accept(this,null).single
        val ctOperator = factory.Core().createBinaryOperator<Any>()
        return ctOperator.apply {
            setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
            setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
            putMetadata<CtBinaryOperator<*>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtBinaryOperatorKind.ELVIS)
            setType<CtBinaryOperator<*>>(referenceBuilder.getNewTypeReference(whenExpression.typeRef))
        }.compose()
    }

    fun visitIfExpression(ifExpression : FirWhenExpression) : CompositeTransformResult.Single<CtIf> {

        val ctIf = factory.Core().createIf()
        val condition = ifExpression.branches[0].condition.accept(this,null).single
        ctIf.setCondition<CtIf>(condition as CtExpression<Boolean>)
        val thenBranch = ifExpression.branches[0].result.accept(this,null).single

        fun branchToStatement(e : CtElement) : CtStatement = when(e) {
            is CtStatement -> e
            is CtExpression<*> -> e.wrapInImplicitReturn()
            else -> throw RuntimeException("Branch is not statement nor expression")
        }

        ctIf.setThenStatement<CtIf>(branchToStatement(thenBranch))

        ifExpression.branches.getOrNull(1)?.let {
            ctIf.setElseStatement<CtIf>(branchToStatement(it.result.accept(this,null).single))
        }

        // Add type
        val type = referenceBuilder.getNewTypeReference<CtType<Any>>(ifExpression.typeRef)
        ctIf.putMetadata<CtIf>(KtMetadataKeys.KT_STATEMENT_TYPE, type)

        return ctIf.compose()
    }

    /*
       for(x in y) { ... }

       >is translated to>

       val <range> = y
       val <iterator> = <range>.iterator
       while(<iterator>.hasNext()) {
        val i = <iterator>.next()
        ...
       }
     */
    override fun visitBlock(block: FirBlock, data: ContextData?): CompositeTransformResult.Single<CtBlock<*>> {
        if(block is FirSingleExpressionBlock) return visitSingleExpressionBlock(block)
        val ktBlock = factory.Core().createBlock<Any>()
        val statements = ArrayList<CtStatement>()
        var context: ContextData? = null
        for(firStatement in block.statements) {
            if(firStatement is FirProperty && firStatement.isLocal && firStatement.name.isSpecial) {
                when(firStatement.name.asString()) {
                    "<range>" -> {
                        context = For(firStatement.initializer!!)
                    }
                    "<destruct>" -> {
                        context = Destruct(firStatement.initializer!!)
                    }
                }
                continue
            }
            val ctElementResult = firStatement.accept(this, context)
            if(ctElementResult.isEmpty) continue
            val ctElement = ctElementResult.single

            statements.add(if(ctElement is CtExpression<*> && ctElement !is CtStatement) {
                ctElement.wrapInImplicitReturn()
            } else if(ctElement is CtBlock<*> && ctElement.statements.size == 1 && ctElement.isImplicit) {
                ctElement.statements[0]
            } else if(ctElement is CtMethod<*>) {
                val wrapperClass = factory.Core().createClass<Any>()
                wrapperClass.apply {
                    setImplicit<CtClass<Any>>(true)
                    setSimpleName<CtClass<*>>("<local>")
                    addMethod<Any, CtClass<Any>>(ctElement as CtMethod<Any>)
                }
            } else {
                ctElement as CtStatement
            })
        }
        ktBlock.setStatements<CtBlock<*>>(statements)
        if(statements.size == 1 && helper.isSingleExpressionBlock(block)) {
            ktBlock.setImplicit<CtBlock<*>>(true)
        }

        ktBlock.putMetadata<CtBlock<*>>(KtMetadataKeys.KT_STATEMENT_TYPE,
            referenceBuilder.getNewTypeReference<CtBlock<*>>(block.typeRef))
        return ktBlock.compose()
    }

    private fun visitSingleExpressionBlock(firBlock: FirSingleExpressionBlock):
        CompositeTransformResult.Single<CtBlock<*>> {
        val ctStatement = statementOrWrappedInImplicitReturn(firBlock.statement.accept(this,null).single)
        if(ctStatement is CtBlock<*> && ctStatement.statements.size == 1) {
            ctStatement.setImplicit<CtBlock<*>>(true)
            return ctStatement.compose()
        }
        val ctBlock = factory.Core().createBlock<Any>()
        ctBlock.setStatements<CtBlock<*>>(listOf(ctStatement))
        ctBlock.putMetadata<CtBlock<*>>(KtMetadataKeys.KT_STATEMENT_TYPE,
            referenceBuilder.getNewTypeReference<CtBlock<*>>(firBlock.typeRef))
        ctBlock.setImplicit<CtBlock<*>>(true)
        return ctBlock.compose()
    }

    private fun CtLoop.exitLoopStatement(firLoop: FirLoop):
            CompositeTransformResult.Single<CtLoop> {
        if(firLoop.label != null)
            setLabel<CtLoop>(firLoop.label!!.name)
        return compose()
    }

    override fun visitWhileLoop(whileLoop: FirWhileLoop, data: ContextData?): CompositeTransformResult<CtElement> {
        when(whileLoop.source?.psi) {
            null -> throw RuntimeException("Unknown source of while loop")
            is KtForExpression -> return visitForLoop(whileLoop,data)
        }
        val ctWhile = factory.Core().createWhile()
        val condition = whileLoop.condition.accept(this,null).single as CtExpression<Boolean>
        val body = whileLoop.block.accept(this,null).single as CtStatement
        return ctWhile.apply {
            setLoopingExpression<CtWhile>(condition)
            setBody<CtWhile>(body)
        }.exitLoopStatement(whileLoop)

    }

    override fun visitDoWhileLoop(doWhileLoop: FirDoWhileLoop, data: ContextData?): CompositeTransformResult<CtElement> {
        val ctDo = factory.Core().createDo()
        val condition = doWhileLoop.condition.accept(this,null).single as CtExpression<Boolean>
        val body = doWhileLoop.block.accept(this,null).single as CtStatement
        return ctDo.apply {
            setLoopingExpression<CtDo>(condition)
            setBody<CtDo>(body)
        }.exitLoopStatement(doWhileLoop)

    }

    fun visitForLoop(forLoop: FirWhileLoop, data: ContextData?): CompositeTransformResult<CtElement> {
        val ctForEach = factory.Core().createForEach()
        val variable = forLoop.block.statements[0].accept(this,null).single as CtLocalVariable<*>
        // Remove initializer ( = next() )
        (variable as CtLocalVariable<Any>).setDefaultExpression<CtLocalVariable<Any>>(null)
        // Loop initialized local vars have no modifiers
        variable.putMetadata<CtLocalVariable<*>>(KtMetadataKeys.KT_MODIFIERS, null)
        ctForEach.setVariable<CtForEach>(variable)
        // Local loop variable has already been handled, remove before visiting
        (forLoop.block.statements as MutableList<FirStatement>).removeAt(0)

        // Add variable, provided by context
        if(data !is For) {
            throw SpoonException("For-loop without iterable")
        } else {
            ctForEach.setExpression(data.iterable.accept(this,null).single as CtExpression<*>)
        }

        val body = forLoop.block.accept(this,null).single as CtStatement
        ctForEach.setBody<CtForEach>(body)
        return ctForEach.exitLoopStatement(forLoop)
    }

    override fun visitOperatorCall(operatorCall: FirOperatorCall, data: ContextData?): CompositeTransformResult<CtElement> {
        val op = factory.Core().createBinaryOperator<Any>()
        val kind = KtBinaryOperatorKind.fromFirOperation(operatorCall.operation)
        op.putMetadata<CtBinaryOperator<Any>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, kind)

        if(operatorCall.arguments.size != 2) throw RuntimeException("Binary operator has ${operatorCall.arguments.size} argument(s)")
        val lhs = operatorCall.arguments[0].accept(this,null).single
        val rhs = operatorCall.arguments[1].accept(this,null).single

        op.setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
        op.setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
        op.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(operatorCall.typeRef))
        return op.compose()
    }

    override fun visitBinaryLogicExpression(
        binaryLogicExpression: FirBinaryLogicExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val op = factory.Core().createBinaryOperator<Boolean>()
        val kind = KtBinaryOperatorKind.firLogicOperationToJavaBinOp(binaryLogicExpression.kind)
        op.setKind<CtBinaryOperator<Boolean>>(kind)

        val lhs = binaryLogicExpression.leftOperand.accept(this,null).single
        val rhs = binaryLogicExpression.rightOperand.accept(this,null).single

        op.setLeftHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(lhs))
        op.setRightHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(rhs))
        op.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(binaryLogicExpression.typeRef))
        return op.compose()
    }

    private fun createUnnamedFunction(function: FirFunction<*>): CtMethod<Any> {
        val ctMethod = factory.Core().createMethod<Any>()

        // Add params
        for (it in function.valueParameters) {
            val p = visitValueParameter(it,null).single
            ctMethod.addParameter<CtMethod<Any>>(p)
        }

        // Add type parameters
        if(function.typeParameters.isNotEmpty()) {
            ctMethod.setFormalCtTypeParameters<CtMethod<*>>(
                function.typeParameters.map { visitTypeParameter(it,null).single })
        }

        // Add body
        val body = function.body
        if(body != null) {
            val ctBody = body.accept(this, null).single
            ctMethod.setBody<CtMethod<Any>>(ctBody as CtStatement)
        }

        // Set (return) type
        ctMethod.setType<CtMethod<*>>(referenceBuilder.getNewTypeReference<Any>(function.returnTypeRef))
        val receiver = function.receiverTypeRef?.accept(this,null)?.single
        receiver?.setParent<CtElement>(ctMethod)
        ctMethod.putMetadata<CtMethod<*>>(KtMetadataKeys.EXTENSION_TYPE_REF, receiver)
        return ctMethod
    }

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: ContextData?): CompositeTransformResult.Single<CtMethod<*>> {
        val ctMethod = createUnnamedFunction(simpleFunction)
        ctMethod.setSimpleName<CtMethod<Any>>(simpleFunction.name.identifier)

        // Add modifiers
        val modifiers = KtModifierKind.fromFunctionDeclaration(simpleFunction)
        addModifiersAsMetadata(ctMethod, modifiers)

        return ctMethod.compose()
    }

    override fun visitThisReceiverExpression(
        thisReceiverExpression: FirThisReceiverExpression,
        data: ContextData?
    ): CompositeTransformResult.Single<CtThisAccess<*>> {
        val implicit = when(thisReceiverExpression.calleeReference) {
            is FirExplicitThisReference -> false
            is FirImplicitThisReference -> true
            else -> false
        }
        val thisAccess = factory.Code().createThisAccess<Any>(
            referenceBuilder.getNewTypeReference(thisReceiverExpression.typeRef),
            implicit)
        return thisAccess.compose()
    }

    override fun visitSuperReference(
        superReference: FirSuperReference,
        data: ContextData?
    ): CompositeTransformResult.Single<CtSuperAccess<*>> {
        val superAccess = factory.Core().createSuperAccess<Any>()
        superAccess.setType<CtSuperAccess<Any>>(referenceBuilder.getNewTypeReference(superReference.superTypeRef))
        superAccess.setImplicit<CtSuperAccess<*>>(false)
        return superAccess.compose()
    }

    private fun visitUnaryExpression(assignment: FirVariableAssignment): CompositeTransformResult.Single<CtUnaryOperator<*>> {
        val ctUnaryOp = factory.Core().createUnaryOperator<Any>()
        val functionCall = assignment.rValue as FirFunctionCall
        val inc = functionCall.calleeReference.name.identifier == "inc"
        val kind = when(assignment.source?.psi) {
            is KtPostfixExpression -> if(inc) UnaryOperatorKind.POSTINC else UnaryOperatorKind.POSTDEC
            is KtPrefixExpression -> if(inc) UnaryOperatorKind.PREINC else UnaryOperatorKind.PREDEC
            else -> null
        }
        ctUnaryOp.setKind<CtUnaryOperator<*>>(kind)
        val target = helper.getReceiver(assignment)?.accept(this, null)?.single as CtExpression<*>?
        val lvalue = assignment.lValue.accept(this,null).single as CtReference
        val operand = createVariableWrite(target, lvalue, assignment.safe)
        ctUnaryOp.setOperand<CtUnaryOperator<*>>(operand)
        ctUnaryOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(assignment.rValue.typeRef))

        return ctUnaryOp.compose()
    }

    private fun createVariableWrite(receiver: CtExpression<*>?, lvalue: CtReference, safe: Boolean) = when (lvalue) {
        is CtLocalVariableReference<*> ->
            factory.Core().createVariableWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(lvalue as CtLocalVariableReference<Any>)
            }
        is CtFieldReference<*> -> {
            factory.Core().createFieldWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(lvalue as CtFieldReference<Any>)
                it.setTarget<CtTargetedExpression<Any, CtExpression<*>>>(receiver)
            }
        }
        is CtParameterReference<*> -> {
            factory.Core().createVariableWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(lvalue as CtParameterReference<Any>)
            }
        }
        else -> throw SpoonException("Unexpected expression ${lvalue::class.simpleName}")
    }.also { it.putMetadata<CtElement>(KtMetadataKeys.ACCESS_IS_SAFE, safe) }

    override fun visitVariableAssignment(
        variableAssignment: FirVariableAssignment,
        data: ContextData?
    ): CompositeTransformResult.Single<CtExpression<*>> {

        if(variableAssignment.source?.psi is KtUnaryExpression)
            return visitUnaryExpression(variableAssignment)
        val ctAssignment = factory.createAssignment<Any, Any>()

        val psiExp = (variableAssignment.source?.psi as? KtBinaryExpression) ?: throw SpoonException("No PSI for variable assignment")
        val opToken = psiExp.operationToken
        if(opToken != KtTokens.EQ)
            return visitAssignmentOperatorViaFunctionCall(
                helper.resolveIfOperatorOrInvocation(variableAssignment.rValue as FirFunctionCall) as InvocationType.ASSIGNMENT_OPERATOR
            )


        val assignmentExpr = variableAssignment.rValue.accept(this, null).single
        ctAssignment.setAssignment<CtAssignment<Any, Any>>(expressionOrWrappedInStatementExpression(assignmentExpr))

        val lvalue = variableAssignment.lValue.accept(this, null).single as CtReference
        val target = helper.getReceiver(variableAssignment)?.accept(this,null)?.single as CtExpression<*>?
        val ctWrite = createVariableWrite(target, lvalue, variableAssignment.safe)
        ctAssignment.setAssigned<CtAssignment<Any, Any>>(ctWrite)

        return ctAssignment.compose()
    }

    override fun visitResolvedNamedReference(
        resolvedNamedReference: FirResolvedNamedReference,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val fir = resolvedNamedReference.resolvedSymbol.fir
        val ctRef = when(fir) {
            is FirProperty -> {
                if(fir.name.isSpecial) return CompositeTransformResult.empty()
                referenceBuilder.getNewVariableReference<CtVariableReference<Any>>(fir)
            }
            is FirValueParameter -> {
                referenceBuilder.getNewVariableReference<CtVariableReference<Any>>(fir)
            }
            else -> null
        }
        if(ctRef != null) return ctRef.compose()
        return super.visitResolvedNamedReference(resolvedNamedReference, data)
    }

    override fun visitValueParameter(
        valueParameter: FirValueParameter,
        data: ContextData?
    ): CompositeTransformResult.Single<CtParameter<*>> {
        val ctParam = factory.Core().createParameter<Any>()
        ctParam.setSimpleName<CtParameter<Any>>(valueParameter.name.identifier)
        ctParam.setInferred<CtParameter<Any>>(false) // Not allowed

        // Modifiers
        val modifierList = KtModifierKind.fromValueParameter(valueParameter)
        addModifiersAsMetadata(ctParam, modifierList)
        ctParam.setVarArgs<CtParameter<Any>>(KtModifierKind.VARARG in modifierList)

        // Default value
        val defaultValue = valueParameter.defaultValue?.accept(this, null)?.single
        if(defaultValue != null) { // TODO Replace with setDefaultExpr
            ctParam.putMetadata<CtParameter<*>>(KtMetadataKeys.PARAMETER_DEFAULT_VALUE, defaultValue)
            defaultValue.setParent(ctParam)
        }

        // Type
        if(valueParameter.isVararg) {
            ctParam.setType<CtParameter<Any>>(helper.resolveVarargType(valueParameter))
        }
        else {
            ctParam.setType<CtParameter<Any>>(referenceBuilder.getNewTypeReference<Any>(valueParameter.returnTypeRef))
        }

        // Implicit? "it" in lambda
        ctParam.setImplicit<CtParameter<*>>(valueParameter.psi == null)

        return ctParam.compose()
    }

    override fun visitTypeParameter(
        typeParameter: FirTypeParameter,
        data: ContextData?
    ): CompositeTransformResult.Single<CtTypeParameter> {
        val ctTypeParameter = factory.Core().createTypeParameter()
        ctTypeParameter.setSimpleName<CtTypeParameter>(typeParameter.name.identifier)

        // Don't include default upper bound ("Any?")
        val refs = typeParameter.bounds.filterNot { it.isNullableAny }.map { referenceBuilder.getNewTypeReference<Any>(it) }
        if(refs.size == 1) {
            ctTypeParameter.setSuperclass<CtTypeParameter>(refs[0])
        } else if(refs.size > 1) {
            ctTypeParameter.setSuperclass<CtTypeParameter>(
                factory.Type().createIntersectionTypeReferenceWithBounds<Any>(refs))
        }

        addModifiersAsMetadata(ctTypeParameter, KtModifierKind.fromTypeVariable(typeParameter))

        return ctTypeParameter.compose()
    }

    override fun visitProperty(property: FirProperty, data: ContextData?): CompositeTransformResult<CtVariable<*>> {
        /*
            a++
            >translates to>
            <unary> = a     // check if we're here, then unary operator will be created in the next visit
            a = <unary>.inc()
            a
         */
        if(property.source?.psi is KtUnaryExpression)
            return CompositeTransformResult.empty()  // Context is responsible for handling this case
        if(property.name.asString() == "_")
            return CompositeTransformResult.empty() // Discarded variable
        if(property.isLocal)
            return visitLocalVariable(property, data)

        val ctProperty = factory.Core().createField<Any>()
        ctProperty.setSimpleName<CtField<*>>(property.name.identifier)

        // Visit and transform initializer
        val transformedExpression = property.initializer?.accept(this, null)
        if(transformedExpression != null && transformedExpression.isSingle) {
            val initializer = transformedExpression.single
            when (initializer) {
                is CtExpression<*> -> {
                    ctProperty.setDefaultExpression<CtField<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(ctProperty)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    ctProperty.setDefaultExpression<CtField<Any>>(statementExpression)
                    initializer.setParent(ctProperty)
                    statementExpression.setParent(ctProperty)
                }
                else -> { /* TODO */  }
            }
        }


        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                ctProperty.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(ctProperty)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(ctProperty, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        ctProperty.setType<CtField<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = helper.hasExplicitTypeDeclaration(property)
        ctProperty.putMetadata<CtField<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)

        // Check if property stems from primary constructor value parameter, in that case this property is implicit
        val initializer = property.initializer
        if(initializer is FirQualifiedAccessExpression &&
            initializer.calleeReference is FirPropertyFromParameterResolvedNamedReference) {
            ctProperty.setImplicit<CtField<*>>(true)
        }

        // Getter
        val getter = property.getter
        if(getter != null && getter !is FirDefaultPropertyGetter && getter.psi != null) {
            ctProperty.putMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_GETTER, visitPropertyAccessor(getter, null ).single)
        }
        // Setter
        val setter = property.setter
        if(setter != null && setter !is FirDefaultPropertySetter && setter.psi != null) {
            ctProperty.putMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_SETTER, visitPropertyAccessor(setter, null ).single)
        }

        return ctProperty.compose()
    }

    override fun visitPropertyAccessor(
        propertyAccessor: FirPropertyAccessor,
        data: ContextData?
    ): CompositeTransformResult.Single<CtMethod<*>> {
        val ctMethod = createUnnamedFunction(propertyAccessor)

        // Add modifiers
        val modifiers = listOfNotNull(
            KtModifierKind.convertVisibility(propertyAccessor.status.visibility),
            KtModifierKind.convertModality(propertyAccessor.status.modality))
        addModifiersAsMetadata(ctMethod, modifiers)
        return ctMethod.compose()
    }

    override fun visitBackingFieldReference(
        backingFieldReference: FirBackingFieldReference,
        data: ContextData?
    ): CompositeTransformResult.Single<CtVariableReference<*>> {
        return referenceBuilder.getNewVariableReference<Any>(backingFieldReference.resolvedSymbol.fir).also {
            it.putMetadata<CtVariableReference<*>>(KtMetadataKeys.IS_ACTUAL_FIELD, true)
        }.compose()
    }

    private fun getBaseOfConst(constExpression: FirConstExpression<Number>) : LiteralBase {
        val text = constExpression.source?.psi?.text
        if(text?.startsWith("0x", ignoreCase = true) == true) return LiteralBase.HEXADECIMAL
        if(text?.startsWith("0b", ignoreCase = true) == true) return LiteralBase.BINARY
        // No octal in Kotlin
        return LiteralBase.DECIMAL
    }

    override fun <T> visitConstExpression(constExpression: FirConstExpression<T>, data: ContextData?): CompositeTransformResult<CtLiteral<T>> {
        val value = when(constExpression.kind) {
            FirConstKind.Int -> (constExpression.value as Long).toInt()
            FirConstKind.Null -> null
            FirConstKind.Boolean -> constExpression.value as Boolean
            FirConstKind.Char -> constExpression.value as Char
            FirConstKind.Byte -> (constExpression.value as Long).toByte()
            FirConstKind.Short -> (constExpression.value as Long).toShort()
            FirConstKind.Long -> constExpression.value as Long
            FirConstKind.String -> constExpression.value as String
            FirConstKind.Float -> constExpression.value as Float
            FirConstKind.Double -> constExpression.value as Double
            FirConstKind.IntegerLiteral -> (constExpression.value as Long).toInt()
        }
        val l : CtLiteral<T> = factory.Core().createLiteral()
        l.setValue<CtLiteral<T>>(value as T)
        if(value == null)
            l.setType<CtLiteral<T>>(factory.Type().nullType() as CtTypeReference<T>)
        else
            l.setType<CtLiteral<T>>(referenceBuilder.getNewTypeReference(constExpression.typeRef))

        if(value is Number) {
            l.setBase<CtLiteral<T>>(getBaseOfConst(constExpression as FirConstExpression<Number>))
            if(constExpression.value is Float || constExpression.value is Double) {
                l.putMetadata<CtLiteral<T>>(KtMetadataKeys.FLOAT_LITERAL_SCIENTIFIC, constExpression.psi?.textContains('e') == true)
            }
        }
        if(constExpression.value is String) {
            // Check if it's multiline
            val openQuote = constExpression.source.psi?.getChildrenOfType<LeafPsiElement>()?.
                firstOrNull { it.node.elementType == KtTokens.OPEN_QUOTE }
            val multiline = "\"\"\"" == openQuote?.text
            l.putMetadata<CtLiteral<T>>(KtMetadataKeys.STRING_LITERAL_MULTILINE, multiline)
        }
        return l.compose()
    }

    fun visitLocalVariable(property: FirProperty, data: ContextData?) : CompositeTransformResult.Single<CtVariable<*>> {
        val localVar = factory.Core().createLocalVariable<Any>().also {
            it.setSimpleName<CtLocalVariable<Any>>(property.name.identifier)
        }

        val transformedExpression = property.initializer?.accept(this, data)
        if(transformedExpression != null && transformedExpression.isSingle) {
            when(val initializer = transformedExpression.single) {
                is CtExpression<*> -> {
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(localVar)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(statementExpression)
                    initializer.setParent(localVar)
                    statementExpression.setParent(localVar)
                }
                else -> { /* TODO */}
            }
        }

        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                localVar.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(localVar)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(localVar, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        localVar.setType<CtLocalVariable<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = helper.hasExplicitTypeDeclaration(property) ?: true // If no PSI source, default to explicit
        localVar.putMetadata<CtLocalVariable<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)
        localVar.setInferred<CtLocalVariable<Any>>(!explicitType)

        return localVar.compose()
    }

    override fun visitAnonymousFunction(
        anonymousFunction: FirAnonymousFunction,
        data: ContextData?
    ): CompositeTransformResult<CtExecutable<*>> {
        val ctLambda = factory.Core().createLambda<Any>()
        val params = anonymousFunction.valueParameters.map { it.accept(this,null).single } as List<CtParameter<*>>
        val body = anonymousFunction.body?.accept(this,null)?.single as CtStatement
        return ctLambda.apply {
            setParameters<CtLambda<Any>>(params)
            setBody<CtLambda<Any>>(body)
            setType<CtLambda<*>>(referenceBuilder.getNewTypeReference(anonymousFunction.returnTypeRef))
            putMetadata<CtLambda<*>>(KtMetadataKeys.LAMBDA_AS_ANONYMOUS_FUNCTION, !anonymousFunction.isLambda)
        }.compose()
    }

    override fun visitLambdaArgumentExpression(
        lambdaArgumentExpression: FirLambdaArgumentExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> = lambdaArgumentExpression.expression.accept(this,null)

    override fun visitReturnExpression(
        returnExpression: FirReturnExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        if(returnExpression.source == null) {
            return returnExpression.result.accept(this,null)
        }

        val ctReturn = factory.Core().createReturn<Any>()
        val ctExpr = returnExpression.result.accept(this,null).single
        if(returnExpression.target.labelName != null) {
            ctReturn.setLabel<CtReturn<*>>(returnExpression.target.labelName)
        }
        ctReturn.setReturnedExpression<CtReturn<Any>>(expressionOrWrappedInStatementExpression(ctExpr) as CtExpression<Any>)

        return ctReturn.compose()
    }

    override fun visitCheckNotNullCall( // a!!
        checkNotNullCall: FirCheckNotNullCall,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val qa = checkNotNullCall.arguments[0].accept(this,null)
        return qa.apply {
            this.single.putMetadata<CtElement>(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL, true) // TODO Make unary operator?
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitQualifiedAccessExpression(
        qualifiedAccessExpression: FirQualifiedAccessExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        val calleeRefRes = qualifiedAccessExpression.calleeReference.accept(this,null)
        if(calleeRefRes.isEmpty) return CompositeTransformResult.empty()
        val calleeRef = calleeRefRes.single
        val target: CtElement? = getReceiver(qualifiedAccessExpression)
        val varAccess = when(calleeRef) {
            is CtFieldReference<*> -> {
                factory.Core().createFieldRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtParameterReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtLocalVariableReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtSuperAccess<*> -> {
                calleeRef
            }
            else -> null
        }
        if(varAccess == null) {
            return super.visitQualifiedAccessExpression(qualifiedAccessExpression, data)
        }

        varAccess.putMetadata<CtVariableRead<*>>(KtMetadataKeys.ACCESS_IS_SAFE, qualifiedAccessExpression.safe)

        if(target != null && varAccess is CtFieldRead<*>) {
            (varAccess as CtFieldRead<Any>).setTarget<CtFieldRead<Any>>(target as CtExpression<*>)
        }

        return varAccess.compose()
    }

    override fun visitTryExpression(
        tryExpression: FirTryExpression,
        data: ContextData?
    ): CompositeTransformResult<CtElement> {
        return factory.Core().createTry().apply {
            setBody<CtTry>(tryExpression.tryBlock.accept(this@FirTreeBuilder,null).single as CtStatement)
            setCatchers<CtTry>(tryExpression.catches.map { it.accept(this@FirTreeBuilder,null).single as CtCatch })
            (tryExpression.finallyBlock?.accept(this@FirTreeBuilder,null)?.single as CtBlock<*>?)?.let { setFinalizer<CtTry>(it) }
        }.compose()
    }

    override fun visitThrowExpression(
        throwExpression: FirThrowExpression,
        data: ContextData?
    ): CompositeTransformResult.Single<CtThrow> {
        val ctThrow = factory.Core().createThrow()
        val throwExpr = throwExpression.exception.accept(this,null).single as CtExpression<Throwable>
        ctThrow.setThrownExpression<CtThrow>(throwExpr)
        return ctThrow.compose()
    }

    override fun visitCatch(catch: FirCatch, data: ContextData?): CompositeTransformResult<CtElement> {
        val block = catch.block.accept(this,null).single as CtStatement
        return factory.Core().createCatch().apply {
            setParameter<CtCatch>(helper.createCatchVariable(catch.parameter))
            setBody<CtCatch>(block)
        }.compose()
    }

    override fun visitBreakExpression(
        breakExpression: FirBreakExpression,
        data: ContextData?
    ): CompositeTransformResult.Single<CtBreak> {
        val ctBreak = factory.Core().createBreak()
        val label = breakExpression.target.labelName
        if(label != null) {
            ctBreak.setTargetLabel<CtBreak>(label)
        }
        return ctBreak.compose()
    }

    override fun visitContinueExpression(
        continueExpression: FirContinueExpression,
        data: ContextData?
    ): CompositeTransformResult.Single<CtContinue> {
        val ctContinue = factory.Core().createContinue()
        val label = continueExpression.target.labelName
        if(label != null) {
            ctContinue.setTargetLabel<CtContinue>(label)
        }
        return ctContinue.compose()
    }

    private fun <T : CtElement> T.compose() = CompositeTransformResult.single(this)
    private fun <T : CtElement> List<CompositeTransformResult<T>>.composeManySingles() = CompositeTransformResult.many(this.map { it.single })
    private fun <T : CtElement> List<CompositeTransformResult<T>>.compose() = CompositeTransformResult.many(this)
    private fun FirClass<*>.isInterface() = this.classKind == ClassKind.INTERFACE
    private fun FirClass<*>.isClass() = this.classKind == ClassKind.CLASS
    private fun FirClass<*>.isObject() = this.classKind == ClassKind.OBJECT
    private fun FirClass<*>.isEnumClass() = this.classKind == ClassKind.ENUM_CLASS
    private fun FirWhenExpression.isIf() = this.psi is KtIfExpression
    private fun <T> CtExpression<T>.wrapInImplicitReturn() : CtReturn<T> {
        val r = factory.Core().createReturn<T>()
        r.setReturnedExpression<CtReturn<T>>(this)
        r.setImplicit<CtReturn<T>>(true)
        return r
    }
    private fun <T> CtStatement.wrapInStatementExpression(type : CtTypeReference<T>) : KtStatementExpression<T> {
        val se = KtStatementExpressionImpl<T>(this)
        se.setType<KtStatementExpression<T>>(type)
        this.setParent(se)
        return se
    }

    private fun statementOrWrappedInImplicitReturn(e: CtElement): CtStatement = when(e) {
        is CtStatement -> e
        is CtExpression<*> -> e.wrapInImplicitReturn()
        else -> throw RuntimeException("Can't wrap ${e::class} in StatementExpression")
    }

    @Suppress("UNCHECKED_CAST")
    private fun expressionOrWrappedInStatementExpression(e: CtElement): CtExpression<Any> {
        val statementExpression: KtStatementExpression<*>
        when (e) {
            is CtExpression<*> -> return e as CtExpression<Any>
            is CtIf -> {
                val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                statementExpression = e.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit(true)
            }
            is CtBlock<*> -> {
                if (e.statements.size == 1) {
                    val statement = e.statements[0]
                    if (e.isImplicit && statement is CtExpression<*>) {
                        return statement as CtExpression<Any>
                    } else {
                        e.setImplicit<CtBlock<*>>(true)
                    }
                }
                val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                statementExpression = e.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit(true)
            }
            else -> throw RuntimeException("Can't wrap ${e::class.simpleName} in StatementExpression")
        }
        return statementExpression
    }
}