package com.example.keyfloat.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/activate")
    fun activate(
        @Field("key") key: String,
        @Field("device_id") deviceId: String
    ): Call<ActivateResponse>
}

data class ActivateResponse(val success: Boolean, val token: String?)
