package com.pgyer.groovy

import com.pgyer.groovy.NetworkUtils.getApiService
import com.pgyer.groovy.Utils.loadGitBranch
import com.pgyer.groovy.bean.DingRequestData
import com.pgyer.groovy.bean.PgyerData
import java.io.File

object NotificationUtils {

    /**
     * 发送钉钉消息
     */
     fun sendMsgToDing(rootDir: File, fileSize: String, data: PgyerData) {
        val content = buildString {
            append("\n\n### 🚀 Android 构建成功，已上传蒲公英")
            append("\n\n**应用**：${data.buildName}")
            append("\n\n**构建时间**：${data.buildUpdated}")
            append("\n\n**更新说明**：${data.buildUpdateDescription}")
            append("\n\n**蒲公英版本**：${data.buildBuildVersion}")
            append("\n\n**包大小**：$fileSize")
            append("\n\n**Android 版本**：${data.buildVersion}")
            append("\n\n**Git 分支**：${loadGitBranch(rootDir)}")
            append("\n\n**下载地址**：https://www.pgyer.com/${data.buildKey}")
            append("\n\n### 📱 扫码下载")
            append("\n\n![](${data.buildQRCodeURL})")
        }

        val req = DingRequestData(
            msgtype = "markdown",
            markdown = DingRequestData.Markdown(
                title = "蒲公英构建通知",
                text = content
            ),
            at = DingRequestData.At(
                atMobiles = mutableListOf("15555787310"),
                atUserIds = mutableListOf("msd_yo0870sp0"),
                isAtAll = false
            )
        )

        val call = getApiService()?.sendDingMsg(Config.DING_NOTICE_URL, req)

        try {
            val response = call?.execute()
            if (response?.isSuccessful == true) {
                println("钉钉发送成功：${response.body()}")
            } else {
                println("钉钉发送失败：${response?.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送微信消息
     */
    fun sendMsgToWeChat(rootDir: File, fileSize: String, data: PgyerData) {

    }

    fun sendMsgToFeishu(rootDir: File, fileSize: String, data: PgyerData) {

    }
}