package com.pgyer.groovy.bean

data class PgyerResult(
    val code: String?="",
    val message: String = "",
    val data: PgyerData?=null
)
