package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.ir.declarations.IrFile

sealed class ContextData(val file: IrFile)

class Empty(file: IrFile) : ContextData(file)