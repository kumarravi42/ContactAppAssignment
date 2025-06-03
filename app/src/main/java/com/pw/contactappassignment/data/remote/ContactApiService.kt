package com.pw.contactappassignment.data.remote

import com.pw.contactappassignment.data.model.ContactApiResp
import retrofit2.Response
import retrofit2.http.GET

interface ContactApiService {
    @GET("api/contacts")
    suspend fun getContacts(): Response<ContactApiResp>
}