package com.pgyer.groovy

import com.pgyer.groovy.Config.legu_apk_path
import com.pgyer.groovy.Config.mac_build_sdk_path
import com.pgyer.groovy.Utils.execCommand
import com.pgyer.groovy.Utils.findApkFile
import com.pgyer.groovy.Utils.isWindows
import java.io.File
import java.io.FileNotFoundException

object PublishUtils {

    // ================== 乐固加固 ==================
    fun reinforce(
        leguJarPath: String,
        legu_app_id: String,
        legu_app_key: String,
        inputPath: String,
        outputPath: String,
    ): File {
        val jarFile = File(leguJarPath)
        if (!jarFile.exists()) {
            throw RuntimeException("jar not exists!")
        }

        val command = if (isWindows()) {
            listOf(
                "powershell", "-Command",
                "java -jar \"$leguJarPath\" -sid $legu_app_id -skey $legu_app_key -uploadPath \"$inputPath\" -downloadPath \"$outputPath\""
            )
        } else {
            listOf(
                "java",
                "-Dfile.encoding=utf-8",
                "-jar", jarFile.absolutePath,
                "-sid", legu_app_id,
                "-skey", legu_app_key,
                "-uploadPath", inputPath,
                "-downloadPath", outputPath
            )
        }

        execCommand(command)
        val apk = findApkFile(outputPath, "_legu")
        if (apk == null) {
            throw RuntimeException("加固完成但未找到 APK 文件")
        }
        return apk
    }

    // ================== zipAlign ==================

    fun zipAlignApk(
        apk: File,
        outputDir: String,
    ): File {
        if (!apk.exists()) {
            throw FileNotFoundException("apk is not exists")
        }

        val outDir = File(outputDir)
        if (!outDir.exists()) outDir.mkdirs()

        val apkName = apk.name.replace(".apk", "_zipalign.apk")
        val outputApk = File(outDir, apkName)

        val command = if (isWindows()) {
            listOf(
                "powershell",
                "-Command",
                "${mac_build_sdk_path}zipalign -f -v -p 4 \"${apk.absolutePath}\" \"${outputApk.absolutePath}\""
            )
        } else {
            listOf(
                "sh",
                "-c",
                "${mac_build_sdk_path}zipalign -f -v -p 4 \"${apk.absolutePath}\" \"${outputApk.absolutePath}\""
            )
        }

        // 3. 执行并校验
        println("开始对齐 APK...")
        execCommand(command)

        if (!outputApk.exists()) {
            throw RuntimeException("Zipalign 执行完成但未生成目标文件")
        }

        return outputApk
    }

    // ================== 签名 ==================
    fun signApkV2(
        signJarPath: String,
        keystorePath: String,
        alias: String,
        ksPassword: String,
        keyPassword: String,
        apk: File,
        outputDir: String,
    ): File {
        if (!apk.exists()) {
            throw FileNotFoundException("apk is not exists")
        }

        val outDir = File(outputDir)
        if (!outDir.exists()) outDir.mkdirs()

        val apkName = apk.name.replace(".apk", "_sign.apk")
        val outputApk = File(outDir, apkName)

        val command = listOf(
            "java", "-jar", signJarPath,
            "sign",
            "--ks", keystorePath,
            "--ks-key-alias", alias,
            "--ks-pass", "pass:$ksPassword",
            "--key-pass", "pass:$keyPassword",
            "--out", outputApk.absolutePath,
            apk.absolutePath
        )

        execCommand(command)
        return outputApk
    }

    // ================== 渠道包 ==================
    fun buildChannelApks(
        walleJarPath: String,
        channelConfigPath: String,
        apk: File,
        outputDir: String,
    ) {
        if (!apk.exists()) {
            throw FileNotFoundException("apk is not exists")
        }

        val outDir = File(outputDir)
        if (!outDir.exists()) outDir.mkdirs()

        val command = if (isWindows()) {
            listOf(
                "powershell", "-Command",
                "java -jar \"$walleJarPath\" batch -f \"$channelConfigPath\" \"${apk.absolutePath}\" \"$outputDir\""
            )
        } else {
            listOf(
                "sh", "-c",
                "java -jar \"$walleJarPath\" batch -f \"$channelConfigPath\" \"${apk.absolutePath}\" \"$outputDir\""
            )
        }
        execCommand(command)
    }
}