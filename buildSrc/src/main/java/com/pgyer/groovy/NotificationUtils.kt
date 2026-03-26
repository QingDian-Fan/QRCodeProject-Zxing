package com.pgyer.groovy

import com.pgyer.groovy.NetworkUtils.getApiService
import com.pgyer.groovy.Utils.loadGitBranch
import com.pgyer.groovy.bean.DingRequestData
import com.pgyer.groovy.bean.FeishuRequest
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

        val call = getApiService()?.sendMsgToDing(Config.DING_NOTICE_URL, req)

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
                content = content
            )
        )

        val call = getApiService()?.sendMsgToWechat(Config.WECHAT_NOTICE_URL, req)

        try {
            val response = call?.execute()
            if (response?.isSuccessful == true) {
                println("企业微信发送成功：${response.body()}")
            } else {
                println("企业微信发送失败：${response?.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMsgToFeishu(rootDir: File, fileSize: String, data: PgyerData) {

        val content = buildString {
            append("**构建应用**:\nAndroid工具包 App")
            append("\n**构建时间**:\n${data.buildUpdated}")
            append("\n**构建描述**:\n${data.buildUpdateDescription}")
            append("\n**蒲公英版本号**:\nbuild${data.buildBuildVersion}")
            append("\n**Android包大小**:\n$fileSize")
            append("\n**Android版本号**:\n${data.buildVersion}")
            append("\n**Git分支**:\n${loadGitBranch(rootDir)}")
            append("\n**下载地址**:\n[https://www.pgyer.com/${data.buildKey}](https://www.pgyer.com/${data.buildKey})")
        }

        val req = FeishuRequest(
            card = FeishuRequest.Card(
                header = FeishuRequest.Header(
                    title = FeishuRequest.Title(
                        content = "Android构建成功,已上传蒲公英"
                    )
                ),
                elements = listOf(

                    // 文本
                    FeishuRequest.Element(
                        tag = "div",
                        text = FeishuRequest.Text(content = content)
                    ),

                    // 按钮
                    FeishuRequest.Element(
                        tag = "action",
                        actions = listOf(
                            FeishuRequest.Button(
                                text = FeishuRequest.Text(content = "下载二维码"),
                                url = data.buildQRCodeURL
                            )
                        )
                    ),

                    // @人
                    FeishuRequest.Element(
                        tag = "div",
                        text = FeishuRequest.Text(
                            content = "<at id=7174239942351290372></at>"
                        )
                    )

                )
            )
        )

        val call = getApiService()?.sendMsgToFeishu(Config.FEISHU_NOTICE_URL, req)

        try {
            val response = call?.execute()
            if (response?.isSuccessful == true) {
                println("飞书发送成功：${response.body()}")
            } else {
                println("飞书发送失败：${response?.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}