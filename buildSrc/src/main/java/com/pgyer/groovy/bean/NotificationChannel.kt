package com.pgyer.groovy.bean

sealed class NotificationChannel {
    data class Ding(val channel: String ="ding"): NotificationChannel()
    data class WeChat(val channel: String ="wechat"): NotificationChannel()
    data class FeiShu(val channel: String ="feishu"): NotificationChannel()
}