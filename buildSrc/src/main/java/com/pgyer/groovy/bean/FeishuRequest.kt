package com.pgyer.groovy.bean

data class FeishuRequest(
    val msg_type: String = "interactive",
    val card: Card
) {

    data class Card(
        val header: Header,
        val elements: List<Element>
    )

    data class Header(
        val title: Title
    )

    data class Title(
        val content: String,
        val tag: String = "plain_text"
    )

    data class Element(
        val tag: String,
        val text: Text? = null,
        val actions: List<Button>? = null
    )

    data class Text(
        val tag: String = "lark_md",
        val content: String
    )

    data class Button(
        val tag: String = "button",
        val text: Text,
        val url: String?,
        val type: String = "default",
        val value: Map<String, String> = emptyMap()
    )
}