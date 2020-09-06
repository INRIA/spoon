package spoon.kotlin.compiler

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.common.modules.ModuleBuilder
import org.jetbrains.kotlin.cli.common.modules.ModuleChunk
import org.jetbrains.kotlin.cli.jvm.compiler.CompileEnvironmentUtil
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.JvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.JvmModulePathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoot
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaModule
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.modules.JavaRootPath
import org.jetbrains.kotlin.modules.Module
import java.io.File

class SpoonKtEnvironment(sourceFiles : List<File>, mName: String?, val args : K2JVMCompilerArguments) {

    private val collector = MessageCollector.NONE
       // PrintingMessageCollector(System.err, MessageRenderer.PLAIN_RELATIVE_PATHS, true)
    private val languageVersion =
        //   LanguageVersionSettingsImpl(LanguageVersion.KOTLIN_1_2, ApiVersion.createByLanguageVersion(LanguageVersion.KOTLIN_1_2))
        LanguageVersionSettingsImpl.DEFAULT
    val config = CompilerConfiguration()
    val moduleChunk : ModuleChunk

    init {
        config.languageVersionSettings = languageVersion
        config.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, collector)
        val moduleName : String = mName ?: JvmProtoBufUtil.DEFAULT_MODULE_NAME
        config.put(CommonConfigurationKeys.MODULE_NAME, moduleName)
        System.setProperty("idea.use.native.fs.for.win", "false")

        val destination = args.destination?.let { File(it) }
        val buildFile = args.buildFile?.let { File(it) }

       // config.configureStandardLibs(null, args)

        moduleChunk = if(buildFile != null) {
            config.put(JVMConfigurationKeys.MODULE_XML_FILE, buildFile)
            CompileEnvironmentUtil.loadModuleChunk(buildFile,collector)
        } else {
            val m = ModuleBuilder(moduleName, destination?.path ?: ".", "java-production")
            args.friendPaths?.forEach { m.addFriendDir(it) }
            args.classpath?.split(File.pathSeparator)?.forEach { m.addClasspathEntry(it) }
            args.javaSourceRoots?.forEach { m.addJavaSourceRoot(JavaRootPath(it, args.javaPackagePrefix)) }
            val commonSources = args.commonSources?.toSet().orEmpty()
            for (arg in args.freeArgs) {
                if (arg.endsWith(".java")) {
                    m.addJavaSourceRoot(JavaRootPath(arg, args.javaPackagePrefix))
                } else {
                    m.addSourceFiles(arg)
                    if (arg in commonSources) {
                        m.addCommonSourceFiles(arg)
                    }

                    if (File(arg).isDirectory) {
                        m.addJavaSourceRoot(JavaRootPath(arg, args.javaPackagePrefix))
                    }
                }
            }
            ModuleChunk(listOf(m))
        }
        configureSourceRoots(config, moduleChunk.modules)
    }


    val ktEnvironment = KotlinCoreEnvironment.createForProduction(
            Disposable {},
            config,
            EnvironmentConfigFiles.JVM_CONFIG_FILES).also {
        if(sourceFiles.isNotEmpty()) {
            it.addKotlinSourceRoots(sourceFiles)
        }
    }
}


private fun configureSourceRoots(configuration: CompilerConfiguration, chunk: List<Module>, buildFile: File? = null) {
    for (module in chunk) {
        val commonSources = module.getCommonSourceFiles().toSet()

        for (path in  module.getSourceFiles()) {
            configuration.addKotlinSourceRoot(path, isCommon = path in commonSources)
        }

        for ((path, packagePrefix) in module.getJavaSourceRoots()) {
            configuration.addJavaSourceRoot(File(path), packagePrefix)
        }
    }

    val isJava9Module = chunk.any { module ->
        module.getJavaSourceRoots().any { (path, packagePrefix) ->
            val file = File(path)
            packagePrefix == null &&
                    (file.name == PsiJavaModule.MODULE_INFO_FILE ||
                            (file.isDirectory && file.listFiles().any { it.name == PsiJavaModule.MODULE_INFO_FILE }))
        }
    }

    for (module in chunk) {
        for (classpathRoot in module.getClasspathRoots()) {
            configuration.add(
                CLIConfigurationKeys.CONTENT_ROOTS,
                if (isJava9Module) JvmModulePathRoot(File(classpathRoot)) else JvmClasspathRoot(File(classpathRoot))
            )
        }

        val modularJdkRoot = module.modularJdkRoot
        if (modularJdkRoot != null) {
            configuration.put(JVMConfigurationKeys.JDK_HOME, File(modularJdkRoot))
            break
        }
    }
    configuration.addAll(JVMConfigurationKeys.MODULES, chunk)
}