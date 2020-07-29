package spoon.test

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import spoon.kotlin.compiler.SpoonKtEnvironment
import spoon.kotlin.compiler.ir.Empty
import spoon.kotlin.compiler.ir.IrGenerator
import spoon.kotlin.compiler.ir.IrTreeBuilder
import spoon.reflect.CtModel
import spoon.reflect.declaration.CtPackage
import spoon.reflect.declaration.CtType
import spoon.reflect.factory.Factory
import spoon.reflect.factory.FactoryImpl
import spoon.support.DefaultCoreFactory
import spoon.support.StandardEnvironment
import java.io.File

object TestBuildUtil {
    val args = K2JVMCompilerArguments().also {
        it.classpath = "./src/test/resources/kotlin-stdlib.jar" // Need stdlib in classpath for FIR
    }

    val baseDir = "./src/test/kotlin"

    fun buildFile(packageName: String, fileName: String): Factory = buildFiles(packageName, listOf(fileName))

    fun buildFiles(packageName: String, fileNames: List<String>): Factory {
        val inputFiles = fileNames.map { File("$baseDir/${packageName.replace('.','/')}/${it}.kt") }
        return buildFiles(inputFiles)
    }

    fun buildFiles(inputFiles: List<File>): Factory {
        val ktEnvironment = SpoonKtEnvironment(inputFiles, "test", args)
        val irGenerator = IrGenerator(ktEnvironment)
        val (irModule, context) = irGenerator.generateIrWithContext()
        val irFiles = irModule.files
        val factory = FactoryImpl(DefaultCoreFactory(), StandardEnvironment())

        val builder = IrTreeBuilder(factory, context)

        irFiles.forEach {  builder.visitFile(it, Empty(it)) }

        factory.model.setBuildModelIsFinished<CtModel>(true)
        return factory
    }

    fun buildClass(packageName: String, className: String): CtType<*> {
        val f = buildFile(packageName, className)
        return f.Package().get(packageName).getType(className)
    }

    fun buildPackage(packageName: String): CtPackage {
        val inputFile = File("$baseDir/${packageName.replace('.','/')}")
        return buildFiles(listOf(inputFile)).Package().get(packageName)
    }
}