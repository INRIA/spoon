package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrVariable

sealed class ContextData(val file: IrFile)

class Empty(file: IrFile) : ContextData(file)
class Destruct(parent: ContextData) : ContextData(parent.file)
class When(parent: ContextData, val subject: IrVariable?) : ContextData(parent.file)
class IgnoreAugmentedOrigin(parent: ContextData) : ContextData(parent.file)