package com.pw.contactappassignment

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {

    private const val BASE_URL: String = "https://android-dev-assignment.onrender.com/"

    private var retrofit: Retrofit? = null

    fun getService(): ContactApiService? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ContactApiService::class.java)
    }

//    val apiInterface: ContactApiService = getService().create(ContactApiService::class.java)

}