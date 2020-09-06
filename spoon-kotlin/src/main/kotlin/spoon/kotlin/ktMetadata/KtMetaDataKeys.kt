package spoon.kotlin.ktMetadata

/**
 *
 */
object KtMetadataKeys {

    const val TYPE_REF_NULLABLE = "skt_key typeref nullable : Boolean"
    const val KT_MODIFIERS = "skt_key kt modifiers : Set<KtModifierKind>"

    const val PROPERTY_DELEGATE = "skt_key property delegate : CtExpression<*>?"
    const val PROPERTY_GETTER = "skt_key property getter : CtMethod<*>?"
    const val PROPERTY_SETTER = "skt_key property setter : CtMethod<*>?"
    const val IS_ACTUAL_FIELD = "skt_key is actual field : Boolean"
    const val VARIABLE_EXPLICIT_TYPE = "skt_key variable explicit type : Boolean"

    const val PARAMETER_DEFAULT_VALUE = "skt_key parameter default value : CtExpression<*>?"

    const val CONSTRUCTOR_DELEGATE_CALL = "skt_key constructor delegate call : CtInvocation<*>?"
    const val CONSTRUCTOR_IS_PRIMARY = "skt_key constructor is primary : Boolean"

    const val KT_STATEMENT_TYPE = "skt_key kt statement type : CtTypeReference<*>"

    const val FLOAT_LITERAL_SCIENTIFIC = "skt_key float literal scientific : Boolean"
    const val STRING_LITERAL_MULTILINE = "skt_key string literal multiline : Boolean"

    const val KT_BINARY_OPERATOR_KIND = "skt_key kt binary operator kind : KtBinaryOperatorKind"
    const val INVOCATION_IS_INFIX = "skt_key invocation is infix : Boolean"

    const val TYPE_CAST_AS_SAFE = "skt_key type cast AS_SAFE : Boolean"
    const val ACCESS_IS_SAFE = "skt_key access is safe : Boolean"

    const val INVOKE_AS_OPERATOR = "skt_key invoke as operator : Boolean"
    const val SET_AS_OPERATOR = "skt_key set as operator : Boolean"

    const val ACCESS_IS_CHECK_NOT_NULL = "skt_key access is assert not null : Boolean"

    const val EXTENSION_TYPE_REF = "skt_key function extension typeref : CtTypeAccess<*>?"
    const val EXTENSION_THIS_TARGET = "skt_key extension this target : String?"

    const val ARRAY_ACCESS_INDEX_ARGS = "skt_key array access args : List<CtExpression>"

    const val LAMBDA_AS_ANONYMOUS_FUNCTION = "skt_key lambda as anonymous function : Boolean"

    const val IMPORT_ALIAS = "skt_key import alias : String?"
    const val TYPE_ALIAS = "skt_key type alias : CtTypeReference<*>?"

    const val NAMED_ARGUMENT = "skt_key named argument : String?"
    const val SPREAD = "skt_key spread : Boolean?"

    const val CLASS_IS_OBJECT = "skt_key class is object : Boolean?"

    const val SUPER_TYPE_DELEGATE = "skt_key super type delegate : CtElement?"

    const val LABEL = "skt_key label : String?"

    const val IS_DESTRUCTURED = "skt_key is destructured : Boolean"
    const val COMPONENTS = "skt_key components : List<CtLocalVariable>?"

    const val IS_CLASS_REFERENCE = "skt_key is class reference: Boolean"
    const val IS_PROPERTY_REFERENCE = "skt_key is property reference: Boolean"

    const val PARAM_IS_VARARG = "skt_key param is vararg: Boolean"
}

