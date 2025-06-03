package com.pw.contactappassignment.data.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("course")
    val course: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("enrolledOn")
    val enrolledOn: String,
    @SerializedName("fullName")
    var fullName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("phone")
    var phone: String
)