package me.adbi.stolosapfma.models

import com.google.gson.annotations.SerializedName

data class PostModel (
    val userId: Int=0,
    val postId: Int=0,
    val title: String="",
    val body: String=""
)