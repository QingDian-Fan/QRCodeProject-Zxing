package com.pgyer.groovy

import com.pgyer.groovy.NetworkUtils.getApiService
import com.pgyer.groovy.bean.PgyerData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object PgyerUtils {

    fun uploaPgyerAPK(msg: String, apkFile: File): PgyerData? {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        //蒲公英账号配置
        val body: MultipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("_api_key", Config.APIKEY)
            .addFormDataPart("userKey", Config.USERKEY)
            .addFormDataPart("buildUpdateDescription", "${msg}${Config.UPDATE_MSG_DESCRIPTION}")
            .addFormDataPart(
                "file",
                apkFile.name,
                apkFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
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
}