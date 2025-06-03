package com.pw.contactappassignment.data.model


import com.google.gson.annotations.SerializedName

data class ContactApiResp(
    @SerializedName("Data")
    val `data`: Data,
    @SerializedName("success")
    val success: Boolean
)