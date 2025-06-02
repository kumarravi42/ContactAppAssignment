package com.pw.contactappassignment

import retrofit2.Response
import retrofit2.http.GET

interface ContactApiService {
    @GET("api/contacts")
    suspend fun getContacts(): Response<List<Contact>>
}