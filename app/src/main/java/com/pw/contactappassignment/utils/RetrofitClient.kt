package com.pw.contactappassignment.utils

import com.pw.contactappassignment.data.remote.ContactApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {

    private const val BASE_URL: String = "https://android-dev-assignment.onrender.com/"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val contactApi = retrofit.create(ContactApiService::class.java)

}