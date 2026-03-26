package com.pgyer.groovy

import com.pgyer.groovy.bean.NotificationChannel

object Config {
    val USERKEY = ""
    val APIKEY = ""

    val PGYER_UPLOAD_URL = "https://api.pgyer.com/apiv2/app/upload"

    val DING_NOTICE_URL =
        ""

    val WECHAT_NOTICE_URL =
        ""

    val FEISHU_NOTICE_URL =
        ""

    val NOTIFICATION_CHANNEL: NotificationChannel = NotificationChannel.Ding()


    val release_apk_path = "/app/build/outputs/apk/release/"
    val legu_apk_path = "/app/build/outputs/apk/release/legu"
    val zipalign_apk_path = "/app/build/outputs/apk/release/zipalign"
    val channel_apk_path = "/app/build/outputs/apk/release/channel"
    val signature_apk_path = "/app/build/outputs/apk/release/signature"
    val legu_app_id = ""
    val legu_app_key = ""
    val legu_jar_path = "/build-tools/ms-shield.jar"
    val sign_jar_path = "/build-tools/apksigner.jar"
    val walle_jar_path = "/build-tools/walle-cli-all.jar"
    val walle_channel_config = "/app/channel"
    val mac_build_sdk_path = "/Users/dian/Library/Android/sdk/build-tools/30.0.2/"

    val keystore_file_path = "/xxxx/xxx.jks"
    val keystore_alias = "xxxx"
    val keystore_alias_password = "xxxxx"
    val keystore_password = "xxx"

    val UPDATE_MSG_DESCRIPTION = "更新说明:xxxxxx"

}
