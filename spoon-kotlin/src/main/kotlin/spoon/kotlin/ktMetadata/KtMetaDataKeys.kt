package spoon.kotlin.ktMetadata

/**
 *
 */
object KtMetadataKeys {

    const val TYPE_REF_NULLABLE = "skt_key typeref nullable : Boolean"
    const val KT_MODIFIERS = "skt_key kt modifiers : Set<KtModifierKind>"

    const val PROPERTY_DELEGATE = "skt_key property delegate : CtExpression<*>?"
    const val VARIABLE_EXPLICIT_TYPE = "skt_key variable explicit type : Boolean"

    const val PARAMETER_DEFAULT_VALUE = "skt_key parameter default value : CtExpression<*>?"

    const val CONSTRUCTOR_DELEGATE_CALL = "skt_key constructor delegate call : CtInvocation<*>?"
    const val CONSTRUCTOR_IS_PRIMARY = "skt_key constructor is primary : Boolean"

    const val KT_IF_TYPE = "skt_key kt if type : CtType<*>"

    const val FLOAT_LITERAL_SCIENTIFIC = "skt_key float literal scientific : Boolean"
    const val STRING_LITERAL_MULTILINE = "skt_key string literal multiline : Boolean"

    const val KT_BINARY_OPERATOR_KIND = "skt_key kt binary operator kind : KtBinaryOperatorKind"
    const val INVOCATION_IS_INFIX = "skt_key invocation is infix : Boolean"
}

