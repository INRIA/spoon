package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirImport
import org.jetbrains.kotlin.fir.declarations.FirResolvedImport
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import spoon.SpoonException
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtReference
import java.lang.StringBuilder

internal class FirImportBuilder(
    private val factory: Factory,
    private val topLvlClassName: String = "<top-level>"
) {
    val errMsg = "No classpath mode not yet supported"

    fun build(files: List<FirFile>) {
        for(file in files) {
            val compilationUnit = factory.CompilationUnit().getOrCreate(file.name)
            compilationUnit.setImports(file.imports.map(this::buildImport))
        }
    }

    private fun buildImport(firImport: FirImport): CtImport {
        return if(firImport is FirResolvedImport) buildResolvedImport(firImport)
            else getNewUnresolvedImport(getImportedFqFromSource(firImport), resolveAlias(firImport))
    }

    private fun buildResolvedImport(resolvedImport: FirResolvedImport): CtImport {
        val importFqName: FqName = getImportedFqFromSource(resolvedImport)
        val alias = resolveAlias(resolvedImport)

        val pkg = factory.Package().get(resolvedImport.packageFqName.asString())

        if(resolvedImport.isAllUnder) { // Ends with '*', e.g. "import p.*"
            if(pkg != null) {
                return getNewImport(pkg.reference, null) // Illegal with alias on * import
            } else {
                throw SpoonException(errMsg)
            }
        } else {
            if(resolvedImport.relativeClassName == null) { // Not importing inner stuff
                val klass = getOrLoadClass(importFqName.asString())
                if(klass == null) { // Check for top-level function or property
                    attemptToGetTopLevelMember(pkg, resolvedImport)?.let {
                        return@buildResolvedImport getNewImport(it.reference, alias)
                    }
                } else {
                    return getNewImport(klass.reference, alias)
                }
            } else { // Importing inner stuff
                val sb = StringBuilder()
                sb.append(resolvedImport.packageFqName.asString())
                sb.append('.')
                sb.append(resolvedImport.relativeClassName!!.asString().replace('.','$'))
                sb.append('$')
                sb.append(importFqName.asString().takeLastWhile { it != '.' })
                val nameWithInnerSep = sb.toString()
                val klass = getOrLoadClass(nameWithInnerSep)
                if(klass != null) {
                    return getNewImport(klass.reference, alias)
                }
            }
        }

        if(factory.environment.noClasspath) {
            return getNewUnresolvedImport(resolvedImport.importedFqName!!, alias)
        }
        throw SpoonException("Error when building imports $importFqName")
    }

    private fun getImportedFqFromSource(firImport: FirImport): FqName {
        if(firImport.importedFqName != null) return firImport.importedFqName!!
        val psi = firImport.psi?.getChildOfType<KtDotQualifiedExpression>() ?: throw SpoonException("Can't get import FQ name")
        return FqName(psi.text)
    }

    /**
     * If an alias is declared and that alias is used, it is found in firImport.aliasName
     * But for an unused import aliasName is still null. Look in PSI if it contains an alias
     */
    private fun resolveAlias(firImport: FirImport): String? {
        if(firImport.aliasName != null) { // Alias is used
            return firImport.aliasName!!.identifier
        }
        // Alias might not exist or just not be used
        val aliasSource = firImport.psi?.getChildOfType<KtImportAlias>() // Source is IMPORT_DIRECTIVE node
        return aliasSource?.allChildren?.lastOrNull { it.node.elementType == KtTokens.IDENTIFIER }?.text
    }

    private fun attemptToGetTopLevelMember(pkg: CtPackage, firImport: FirImport): CtNamedElement? {
        val klass = pkg.getType<CtType<*>>("<top-level>")
        val entityName = getImportedFqFromSource(firImport).shortName().identifier
        return klass?.let { it.getField(entityName) ?: it.getMethodsByName(entityName)?.getOrNull(0) }
    }

    private fun getNewImport(ctReference: CtReference, alias: String?): CtImport {
        return factory.Type().createImport(ctReference).apply {
            putMetadata<CtImport>(KtMetadataKeys.IMPORT_ALIAS, alias)
        }
    }

    private fun getNewUnresolvedImport(fqName: FqName, alias: String?): CtImport {
        return factory.Type().createUnresolvedImport(fqName.asString(), false).apply {
            putMetadata<CtImport>(KtMetadataKeys.IMPORT_ALIAS, alias)
        }
    }

    private fun getOrLoadClass(className: String): CtType<*>? {
        return  factory.Type().get<CtClass<*>>(className)       ?:
                factory.Interface().get<CtClass<*>?>(className) ?:
        return try {
            val loadedClass = this::class.java.classLoader.loadClass(className)
            factory.Type().get<CtClass<*>>(loadedClass)
        } catch (e: NoClassDefFoundError) {
            if(className.contains(CtPackage.PACKAGE_SEPARATOR_CHAR)) {
                val classWithInnerSep = className.reversed().replaceFirst('.','$').reversed()
                return getOrLoadClass(classWithInnerSep)
            }
            null
        } catch (e: ClassNotFoundException) {
            if(className.contains(CtPackage.PACKAGE_SEPARATOR_CHAR)) {
                val classWithInnerSep = className.reversed().replaceFirst('.','$').reversed()
                return getOrLoadClass(classWithInnerSep)
            }
            null
        }
    }

}