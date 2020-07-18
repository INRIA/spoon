package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.psi2ir.Psi2IrConfiguration
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator
import org.jetbrains.kotlin.psi2ir.generators.GeneratorExtensions
import spoon.kotlin.compiler.SpoonKtEnvironment

class IrGenerator(ktEnvironment: SpoonKtEnvironment) {
    val environment = ktEnvironment.ktEnvironment
    val config = ktEnvironment.config
    private fun analyzePsi(environment: KotlinCoreEnvironment) : org.jetbrains.kotlin.analyzer.AnalysisResult {
        val sourceFiles = environment.getSourceFiles()
        val collector = environment.configuration.getNotNull(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)
        val analyzer = AnalyzerWithCompilerReport(collector, environment.configuration.languageVersionSettings)
        analyzer.analyzeAndReport(sourceFiles) {
            //   val sourcesOnly = TopDownAnalyzerFacadeForJVM.newModuleSearchScope(environment.project, sourceFiles)
            TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(environment.project,
                sourceFiles,
                NoScopeRecordCliBindingTrace(),
                environment.configuration,
                environment::createPackagePartProvider
            )
        }
        return analyzer.analysisResult

    }

    fun generateIr() : IrModuleFragment {
        val a = analyzePsi(environment)

        val translator = Psi2IrTranslator(config.languageVersionSettings, Psi2IrConfiguration(true))
        return translator.generateModule(a.moduleDescriptor,environment.getSourceFiles(),a.bindingContext, GeneratorExtensions())
    }
}