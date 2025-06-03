package com.pw.contactappassignment.data.remote.repo

import com.pw.contactappassignment.data.model.User
import com.pw.contactappassignment.data.remote.ContactApiService

class ContactRepository(private val api: ContactApiService) {
    suspend fun fetchContacts(): List<User> {
        val res = api.getContacts()

        return (if (res.isSuccessful && res.body() != null)
            res.body()!!.data.users
        else
            emptyList())
    }
}