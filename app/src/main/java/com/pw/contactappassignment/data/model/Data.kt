package com.pw.contactappassignment.data.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("date")
    val date: String,
    @SerializedName("totalUsers")
    val totalUsers: Int,
    @SerializedName("users")
    val users: List<User>
)