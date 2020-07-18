package spoon.kotlin.compiler.fir


import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.builder.RawFirBuilder
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.resolve.transformers.FirTotalResolveTransformer
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files.createTempFile
import java.nio.file.Path

private typealias ClassWrapperMap = MutableMap<ClassId, SuperTypeDelegateFinder.ClassWrapper>
internal object SuperTypeDelegateFinder {

    fun resolve(firFiles: List<FirFile>, config: CompilerConfiguration): Map<ClassId, MutableMap<ClassId, FirStatement>> {
        val classWrapperMap: ClassWrapperMap = mutableMapOf()
        val tmpFiles = ArrayList<File>()
        for(file in firFiles) {
            val fileClassWrapperMap: ClassWrapperMap = mutableMapOf()
            fillClassWrapperMap(
                file.declarations,
                fileClassWrapperMap
            )
            val originalKtFile = file.psi as KtFile? ?: throw RuntimeException("Can't get KtFile")
            val tmpNewFilePath: Path = createTempFile("delegateResolve",".kt")
            val tmpNewFile = tmpNewFilePath.toFile()
            tmpNewFile.deleteOnExit()

            val tmpFileWriter = BufferedWriter(FileWriter(tmpNewFile))
            val originalContent = originalKtFile.text.replace(System.lineSeparator(),"\n")

            val newContentSb = StringBuilder()
            copyAndInsert(
                originalContent,
                newContentSb,
                fileClassWrapperMap.values.toList()
            )
            tmpFileWriter.write(newContentSb.toString().replace("\n",System.lineSeparator()))
            tmpFileWriter.flush()
            tmpFiles.add(tmpNewFile)
            classWrapperMap.putAll(fileClassWrapperMap)
        }

        if(classWrapperMap.isEmpty() || classWrapperMap.none { it.value.hasDelegates }) return emptyMap()

        val newEnvironment = KotlinCoreEnvironment.createForProduction(
            Disposable { },
            config, EnvironmentConfigFiles.JVM_CONFIG_FILES)
        newEnvironment.addKotlinSourceRoots(tmpFiles)
        val builder = RawFirBuilder(firFiles[0].session,false)
        val resolveTransformer = FirTotalResolveTransformer()

        val newFirFiles = newEnvironment.getSourceFiles().map {
            builder.buildFirFile(it)
        }.also {
            resolveTransformer.processFiles(it)
        }

        val delegateToFirMap = mutableMapOf<ClassId, MutableMap<ClassId, FirStatement>>()

        for(tmpFirFile in newFirFiles) {
            allClassesWithFirDelegate(
                tmpFirFile.declarations,
                classWrapperMap,
                delegateToFirMap
            )
        }

        for(tmpFile in tmpFiles)
            tmpFile.delete()

        return delegateToFirMap
    }

    private fun copyAndInsert(originalContent: String, newStringBuilder: StringBuilder, exprs: List<ClassWrapper>) {
        val initsByOffset = exprs.sortedBy { it.bodyOffset }

        var nextOffset = initsByOffset.getOrNull(0)?.bodyOffset ?: Int.MAX_VALUE
        var currentPos = 0
        var currentInit = 0

        while(currentPos < originalContent.length) {
            while(currentPos < nextOffset && currentPos < originalContent.length) {
                newStringBuilder.append(originalContent[currentPos++])
            }
            if(currentPos < originalContent.length && currentInit < initsByOffset.size) {
                insertInitBlock(
                    newStringBuilder,
                    initsByOffset[currentInit++]
                )
                nextOffset = if(currentInit >= initsByOffset.size) {
                    Int.MAX_VALUE
                } else {
                    initsByOffset[currentInit].bodyOffset
                }
            }
        }
    }

    private fun insertInitBlock(sb: StringBuilder, wrapper: ClassWrapper) {
        if(!wrapper.hasDelegates) return
        if(wrapper.mustWrap) sb.append("\n{")
        sb.append("init {\n")
        val sortedTexts = wrapper.delegateTexts.values.toList().sortedBy { it.first }
        for(text in sortedTexts) {
            sb.append(text.second)
            sb.append("\n")
        }
        sb.append("}\n")
        if(wrapper.mustWrap) sb.append('}')
    }

    private fun allClassesWithFirDelegate(classDeclarations: List<FirDeclaration>,
                                          classWrapperMap: MutableMap<ClassId, ClassWrapper>,
                                          refToFirExprMap: MutableMap<ClassId, MutableMap<ClassId, FirStatement>>
    ) {
        for(decl in classDeclarations) {
            if(decl is FirClass<*>) {
                val wrapper = classWrapperMap[decl.classId]
                if(wrapper != null) {
                    val initializer = decl.declarations.firstIsInstanceOrNull<FirAnonymousInitializer>()!!
                    assert(initializer.body!!.statements.size == wrapper.delegateTexts.size)
                    for (superType in decl.superConeTypes) {
                        val delegatePair = wrapper.delegateTexts[superType.classId!!]
                        if (delegatePair != null) {
                            val firExpr = initializer.body!!.statements[delegatePair.first]
                            refToFirExprMap.putIfAbsent(decl.classId, mutableMapOf())
                            refToFirExprMap[decl.classId]!![superType.classId!!] = firExpr
                        }
                    }
                }
                allClassesWithFirDelegate(
                    decl.declarations,
                    classWrapperMap,
                    refToFirExprMap
                )
            }
        }
    }

    private fun fillClassWrapperMap(classDeclarations: List<FirDeclaration>, map: MutableMap<ClassId, ClassWrapper>) {
        for(decl in classDeclarations) {
            if(decl is FirClass<*>) {
                var delegateIndex = 0
                val superTypeMap = mutableMapOf<ClassId, Pair<Int,String>>()
                for(superType in decl.superTypeRefs) {
                    if (superType.isAny) {
                        continue
                    }
                    if(superType.psi == null &&
                        superType.coneTypeSafe<ConeClassLikeType>()?.classId?.asSingleFqName()?.asString() == "kotlin.Enum") {
                        continue
                    }
                    val psi = superType.psi ?:
                        throw RuntimeException("Unable to get PSI when resolving supertype expression for $superType")
                    val parent = psi.parent
                    if(parent is KtDelegatedSuperTypeEntry) {
                        val text = parent.delegateExpression?.text ?:
                            throw RuntimeException("Unable to get delegate expression")
                        superTypeMap[superType.coneTypeUnsafe<ConeClassLikeType>().classId!!] = delegateIndex++ to text
                    }
                }
                if(superTypeMap.isNotEmpty()) {
                    val classPsi = decl.psi as? KtClassOrObject ?: throw RuntimeException("Unable to get class PSI")
                    val body = classPsi.body
                    val offset = if(body == null) {
                        classPsi.endOffset
                    } else {
                        body.startOffset + 1
                    }
                    map[decl.classId] =
                        ClassWrapper(
                            superTypeMap,
                            offset,
                            body == null
                        )
                }
                fillClassWrapperMap(
                    decl.declarations,
                    map
                )
            }
        }
    }

    data class ClassWrapper(
        val delegateTexts: Map<ClassId, Pair<Int,String>>,
        val bodyOffset: Int,
        val mustWrap: Boolean
    ) {
        val numSuperTypes get() = delegateTexts.size
        val hasDelegates get() = delegateTexts.isNotEmpty()
    }
}



