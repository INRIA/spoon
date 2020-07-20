package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.generateTypicalIrProviderList
import org.jetbrains.kotlin.psi2ir.Psi2IrConfiguration
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator
import org.jetbrains.kotlin.psi2ir.generators.GeneratorContext
import spoon.kotlin.compiler.SpoonKtEnvironment

internal class IrGenerator(ktEnvironment: SpoonKtEnvironment) {
    val environment = ktEnvironment.ktEnvironment
    val config = ktEnvironment.config
    private val translator= Psi2IrTranslator(config.languageVersionSettings, Psi2IrConfiguration(true))
    private val analysisResult by lazy { analyzePsi(environment) }

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

    fun generateIrWithContext(): Pair<IrModuleFragment,GeneratorContext> {
        val context = translator.createGeneratorContext(
            analysisResult.moduleDescriptor,
            analysisResult.bindingContext
        )
        val irProviders = generateTypicalIrProviderList(
            analysisResult.moduleDescriptor,
            context.irBuiltIns,
            context.symbolTable

        )
        return translator.generateModuleFragment(context,
        environment.getSourceFiles(), irProviders) to context
    }
}