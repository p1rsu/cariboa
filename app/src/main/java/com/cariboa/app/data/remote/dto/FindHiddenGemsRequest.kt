package com.cariboa.app.data.remote.dto

data class FindHiddenGemsRequest(
    val destination: String,
    val categories: List<String>? = null,
)
