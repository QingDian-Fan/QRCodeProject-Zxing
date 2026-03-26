package com.pgyer.groovy.bean


data class DingRequestData(
    val at: At? = null,
    val msgtype: String = "text",
    val markdown: Markdown?=null
) {

    data class At(
        val atMobiles: List<String>,
        val atUserIds: List<String>?,
        val isAtAll: Boolean
    )

    data class Markdown(
        val title: String,
        val text: String?=null,
        val content: String?=null
    )
}