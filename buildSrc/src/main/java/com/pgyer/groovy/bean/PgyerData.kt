package com.pgyer.groovy.bean

data class PgyerData(
    val buildKey: String?="",
    val buildType: String?="",
    val buildIsFirst: String?="",
    val buildIsLastest: String?="",
    val buildFileKey: String?="",
    val buildFileName: String?="",
    val buildFileSize: String?="",
    val buildName: String?="",
    val buildVersion: String?="",
    val buildVersionNo: String?="",
    val buildBuildVersion: String?="",
    val buildIdentifier: String?="",
    val buildIcon: String?="",
    val buildDescription: String?="",
    val buildUpdateDescription: String?="",
    val buildScreenshots: String?="",
    val buildShortcutUrl: String?="",
    val buildCreated: String?="",
    val buildUpdated: String?="",
    val buildQRCodeURL: String?="",
)
/*
{
  "code": 0.0,
  "message": "",
  "data": {
    "buildKey": "46d00dfb99cc16911eb766638c987673",
    "buildType": 2,
    "buildIsFirst": 0,
    "buildIsLastest": 1,
    "buildFileKey": "b722090c5c5131c0ecaa5038af81e142.apk",
    "buildFileName": "app-debug.apk",
    "buildFileSize": 16598287,
    "buildName": "QR-Code",
    "buildVersion": "1.0",
    "buildVersionNo": "1",
    "buildBuildVersion": "2",
    "buildIdentifier": "com.android.qrcode",
    "buildIcon": "67be1a5b851ac06514a227f1cabfc150",
    "buildDescription": "",
    "buildUpdateDescription": "2026-03-23 05:14:25 == app-debug.apk",
    "buildScreenshots": "",
    "buildShortcutUrl": "qrcode-android-z",
    "buildCreated": "2026-03-23 17:14:37",
    "buildUpdated": "2026-03-23 17:14:37",
    "buildQRCodeURL": "https://pgyer.com/app/qrcodeHistory/adf4ca288b7d94e00f86fc65acf4025606e17325e7f3f96d9c5f358929cf15d0"
  }
}
 */