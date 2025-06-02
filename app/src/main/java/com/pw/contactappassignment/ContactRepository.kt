package com.pw.contactappassignment

class ContactRepository(private val api: ContactApiService) {
    suspend fun fetchContacts(): List<Contact>? {
        val res = api.getContacts()
        return if (res.isSuccessful) res.body() else null
    }
}