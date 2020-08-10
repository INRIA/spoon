package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.cu.CompilationUnit
import spoon.reflect.declaration.CtImport
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.declaration.CtPackage
import spoon.reflect.declaration.CtType
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtReference

class PsiImportBuilder(
    private val factory : Factory
) {

    fun build(files: List<KtFile>) {
        for(file in files) {
            val cu = factory.CompilationUnit().getOrCreate(file.virtualFilePath)
            cu.setImports(file.importDirectives.map { resolveImport(it, cu) })
        }
    }

    private fun resolveImport(importDirective: KtImportDirective, cu: CompilationUnit): CtImport {
        if(importDirective.isAllUnder) { // import a.b.* ==> importFqName = a.b
            val pkg = factory.Package().get(importDirective.importedFqName!!.asString())
            return if(pkg != null) {
                createImportWithPosition(pkg.reference, importDirective, cu)
            } else {
                createUnresolvedImportWithPosition(importDirective, cu)
            }
        }

        var currentRoot: CtNamedElement? = null
        for(segment in importDirective.importedFqName!!.pathSegments().map { it.asString() }) {
            when(currentRoot) {
                null -> {
                    val nextRoot = factory.Package().get(segment)
                        ?: factory.Type().get<CtType<*>>(segment)
                        ?: factory.Interface().get<CtType<*>>(segment)
                        ?: attemptToGetTopLevelMember(factory.Package().rootPackage, importDirective)
                    currentRoot = nextRoot
                }
                is CtPackage -> {
                    val nextRoot = currentRoot.getPackage(segment)
                        ?: currentRoot.getType(segment)
                        ?: attemptToGetTopLevelMember(currentRoot, importDirective)
                    currentRoot = nextRoot
                }
                is CtType<*> -> {
                    val nextRoot = currentRoot.getField(segment)
                        ?: currentRoot.getMethodsByName(segment).getOrNull(0)
                        ?: currentRoot.getNestedType<CtType<*>>(segment)
                    currentRoot = nextRoot
                }
            }
            if(currentRoot == null) {
                return createUnresolvedImportWithPosition(importDirective, cu)
            }
        }
        return createImportWithPosition(currentRoot!!.reference, importDirective, cu)
    }


    private fun attemptToGetTopLevelMember(pkg: CtPackage, importDirective: KtImportDirective): CtNamedElement? {
        val klass = pkg.getType<CtType<*>>("<top-level>") ?: return null
        val entityName = importDirective.importedName?.asString() ?: return null
        return klass.getField(entityName) ?: klass.getMethodsByName(entityName)?.getOrNull(0)
    }

    private fun createImportWithPosition(ref: CtReference, importDirective: KtImportDirective, cu: CompilationUnit): CtImport {
        val ctImport = factory.Type().createImport(ref)
        ctImport.setPosition<CtImport>(factory.Core().createSourcePosition(
            cu,
            importDirective.startOffset,
            importDirective.endOffset,
            cu.lineSeparatorPositions
        ))
        ctImport.reference.setPosition<CtImport>(factory.Core().createSourcePosition(
            cu,
            importDirective.startOffset,
            importDirective.endOffset,
            cu.lineSeparatorPositions
        ))
        ctImport.putMetadata<CtImport>(KtMetadataKeys.IMPORT_ALIAS, importDirective.aliasName)
        return ctImport
    }

    private fun createUnresolvedImportWithPosition(importDirective: KtImportDirective, cu: CompilationUnit): CtImport {
        val ctImport = factory.Type().createUnresolvedImport(importDirective.importPath!!.pathStr, false)
        ctImport.setPosition<CtImport>(factory.Core().createSourcePosition(
            cu,
            importDirective.startOffset,
            importDirective.endOffset,
            cu.lineSeparatorPositions
        ))
        ctImport.putMetadata<CtImport>(KtMetadataKeys.IMPORT_ALIAS, importDirective.aliasName)
        return ctImport
    }
}