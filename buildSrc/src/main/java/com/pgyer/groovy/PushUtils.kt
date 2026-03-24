package com.pgyer.groovy

import com.pgyer.groovy.bean.DingRequestData
import com.pgyer.groovy.bean.PgyerData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PushUtils {
    var mApiService: ApiService? = null

    fun getApiService(): ApiService? {
        mApiService ?: run {
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    println("http==>: $message")
                }
            })
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .hostnameVerifier { hostname, session -> true }
                .build()
            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://oapi.dingtalk.com/robot/")
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
            mApiService = retrofit.create(ApiService::class.java)
        }
        return mApiService
    }

    fun uploaPgyerAPK(msg:String,apkFile: File): PgyerData? {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        //蒲公英账号配置
        val body: MultipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("_api_key", Config.APIKEY)
            .addFormDataPart("userKey", Config.USERKEY)
            .addFormDataPart("buildUpdateDescription", "${msg}${Config.UPDATE_MSG_DESCRIPTION}" )
            .addFormDataPart("file", apkFile.name, apkFile.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
            .build()
        val call = getApiService()?.uploadPgyerAPK(Config.PGYER_UPLOAD_URL, body)
        return try {
            val response = call?.execute()
            if (response?.isSuccessful == true) {
                response.body()?.data
            } else {
                println("upload error: ${response?.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun sendDingMsg(rootDir: File,fileSize: String, data: PgyerData) {

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

    fun doTask(rootDir:File,msg:String,apkFile: File) {
        val responseData = uploaPgyerAPK(msg,apkFile)
        responseData?.let {
            sendDingMsg(rootDir,getFileSize(apkFile),it)
        }
    }

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
            val headFile = File(rootDir,".git/HEAD")  // 相对项目根目录
            val content = headFile.readText().trim()

            if (content.startsWith("ref:")) {
                content.removePrefix("ref: refs/heads/")
            } else {
                content.take(7)
            }
        }.getOrDefault("unknown")
}
