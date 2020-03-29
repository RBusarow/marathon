package com.malinskiy.marathon.extensions

import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.LibraryVariantOutput
import com.android.build.gradle.api.TestVariant
import com.android.build.gradle.tasks.PackageAndroidArtifact
import org.apache.tools.ant.taskdefs.Zip
import org.gradle.api.GradleException
import java.io.File

fun TestVariant.extractTestApplication() = extractTestApplication3_3_plus(this)
//    com.malinskiy.marathon.extensions.executeGradleCompat(
//    exec = {
//        extractTestApplication3_3_plus(this)
//    },
//    fallback = {
//        extractTestApplicationBefore3_3(this)
//    }
//)

private fun extractTestApplicationBefore3_3(variant: TestVariant): File {
    val output = variant.outputs.first()

    return File(
        when (output) {
            is ApkVariantOutput -> {
                @Suppress("DEPRECATION")
                File(variant.packageApplication.outputDirectory.asFile.get(), output.outputFileName).path
            }
            is LibraryVariantOutput -> {
                output.outputFile.path
            }
            else -> {
                throw RuntimeException("Can't find instrumentationApk")
            }
        }
    )
}

private fun extractTestApplication3_3_plus(variant: TestVariant): File {

//    val output = variant.outputs.find { it is ApkVariantOutput } as? ApkVariantOutput
//        ?: throw IllegalArgumentException("Can't find APK output")
//    val packageTask = variant.packageApplicationProvider.orNull
//        ?: throw IllegalArgumentException("Can't find package application provider")
//
//    println("file -------------------------->" + File(packageTask.outputDirectory.asFile.get(), output.outputFileName))
//
//    return File(packageTask.outputDirectory.asFile.get(), output.outputFileName)

    val apkOutput = variant.outputs.find { it is ApkVariantOutput } as? ApkVariantOutput

    require(apkOutput != null) { "Can't find APK output" }

    val packageAppProvider = variant.packageApplicationProvider.get()

    return when (packageAppProvider) {
        is PackageAndroidArtifact -> {
            File(packageAppProvider.outputDirectory.asFile.get(), apkOutput.outputFileName)
        }
        is Zip -> {
            packageAppProvider.destFile
        }
        else -> throw GradleException("Unknown artifact package type")
    }
}
