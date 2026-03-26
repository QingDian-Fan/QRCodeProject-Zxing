package com.pgyer.groovy

import java.io.File

object Utils {
     fun getFileSize(file: File): String {
        val size = file.length()
        return when {
            size < 1024 -> "%.1fB".format(size.toDouble())
            size < 1024 * 1024 -> "%.1fKB".format(size / 1024.0)
            else -> "%.1fMB".format(size / 1024.0 / 1024.0)
        }
    }



     fun loadGitBranch(rootDir: File): String =
        runCatching {
            val headFile = File(rootDir, ".git/HEAD")  // 相对项目根目录
            val content = headFile.readText().trim()

            if (content.startsWith("ref:")) {
                content.removePrefix("ref: refs/heads/")
            } else {
                content.take(7)
            }
        }.getOrDefault("unknown")




    fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase().contains("windows")
    }


     fun execCommand(command: List<String>) {
        println("执行命令: ${command.joinToString(" ")}")

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        println(output)

        if (exitCode != 0) {
            throw RuntimeException("命令执行失败，exitCode=$exitCode")
        }
    }



    /**
     * 注释：查找apk文件
     */
    fun findApkFile(path: String, suffix: String): File? {
        val dir = File(path)
        // 使用 listFiles() 获取文件数组，并使用 find 扩展函数进行过滤
        return dir.listFiles()?.find {
            it.isFile && it.name.endsWith("$suffix.apk")
        }
    }
}