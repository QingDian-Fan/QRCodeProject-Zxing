package com.pgyer.groovy

import com.pgyer.groovy.bean.DingRequestData
import com.pgyer.groovy.bean.FeishuRequest
import com.pgyer.groovy.bean.PgyerResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST
    fun sendMsgToDing(
        @Url url: String,
        @Body req: DingRequestData): Call<Any>

    @POST
    fun sendMsgToWechat(
        @Url url: String,
        @Body req: DingRequestData): Call<Any>

    @POST
    fun sendMsgToFeishu(
        @Url url: String,
        @Body req: FeishuRequest
    ): Call<Any>


    @POST
    fun uploadPgyerAPK(
        @Url url: String,
        @Body body: RequestBody
    ): Call<PgyerResult>


}