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
}