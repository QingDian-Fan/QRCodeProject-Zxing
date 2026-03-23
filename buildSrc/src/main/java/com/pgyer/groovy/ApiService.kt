package com.pgyer.groovy

import com.pgyer.groovy.bean.DingRequestData
import com.pgyer.groovy.bean.PgyerResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST
    fun sendDingMsg(
        @Url url: String,
        @Body req: DingRequestData): Call<Any>


    @POST
    fun uploadPgyerAPK(
        @Url url: String,
        @Body body: RequestBody
    ): Call<PgyerResult>


}