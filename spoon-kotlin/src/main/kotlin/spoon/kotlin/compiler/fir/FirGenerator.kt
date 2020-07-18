package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.com.intellij.psi.search.ProjectScope
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.builder.RawFirBuilder
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.java.FirJavaModuleBasedSession
import org.jetbrains.kotlin.fir.java.FirLibrarySession
import org.jetbrains.kotlin.fir.java.FirProjectSessionProvider
import org.jetbrains.kotlin.fir.resolve.firProvider
import org.jetbrains.kotlin.fir.resolve.impl.FirProviderImpl
import org.jetbrains.kotlin.fir.resolve.transformers.FirTotalResolveTransformer
import org.jetbrains.kotlin.modules.Module
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.resolve.PlatformDependentAnalyzerServices
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatformAnalyzerServices

/**
 * Generates FirFiles.
 * Environment shall contain the source files to be parsed.
 *
 * This class envelopes what is basically a copy-paste approach from how the Kotlin compiler generates FIR
 */
internal class FirGenerator(private val environment: KotlinCoreEnvironment, private val module: Module) {
    private val targetPlatform = JvmPlatforms.jvm18 // Default = 1.6

    fun generateFIR() : List<FirFile> {
        val ktFiles = environment.getSourceFiles()
        val project = environment.project
        val scope = GlobalSearchScope.filesScope(project, ktFiles.map { it.virtualFile })
                .uniteWith(TopDownAnalyzerFacadeForJVM.AllJavaSourcesInProjectScope(project))
        val provider = FirProjectSessionProvider(project)
        val moduleInfo = FirJvmModuleInfo(module.getModuleName())
        val session: FirSession = FirJavaModuleBasedSession(moduleInfo, provider, scope).also {
            val dependenciesInfo = FirJvmModuleInfo(Name.special("<dependencies>"))
            moduleInfo.dependencies.add(dependenciesInfo)
            val librariesScope = ProjectScope.getLibrariesScope(project)
            FirLibrarySession.create(
                    dependenciesInfo, provider, librariesScope,
                    project, environment.createPackagePartProvider(librariesScope)
            )

        }
        val firProvider = (session.firProvider as FirProviderImpl)
        val builder = RawFirBuilder(session, stubMode = false)
        val resolveTransformer = FirTotalResolveTransformer()
        return ktFiles.map {
            val firFile = builder.buildFirFile(it)
            firProvider.recordFile(firFile)
            firFile
        }.also {
            try {
                resolveTransformer.processFiles(it)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private inner class FirJvmModuleInfo(override val name: Name) : ModuleInfo {
        constructor(moduleName: String) : this(Name.identifier(moduleName))

        val dependencies: MutableList<ModuleInfo> = mutableListOf()

        override val platform: TargetPlatform
            get() = targetPlatform

        override val analyzerServices: PlatformDependentAnalyzerServices
            get() = JvmPlatformAnalyzerServices

        override fun dependencies(): List<ModuleInfo> {
            return dependencies
        }
    }
}