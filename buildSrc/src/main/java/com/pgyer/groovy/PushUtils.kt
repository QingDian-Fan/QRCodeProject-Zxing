package com.pgyer.groovy

import com.pgyer.groovy.Config.channel_apk_path
import com.pgyer.groovy.Config.keystore_alias
import com.pgyer.groovy.Config.keystore_alias_password
import com.pgyer.groovy.Config.keystore_file_path
import com.pgyer.groovy.Config.keystore_password
import com.pgyer.groovy.Config.legu_apk_path
import com.pgyer.groovy.Config.legu_app_id
import com.pgyer.groovy.Config.legu_app_key
import com.pgyer.groovy.Config.legu_jar_path
import com.pgyer.groovy.Config.release_apk_path
import com.pgyer.groovy.Config.sign_jar_path
import com.pgyer.groovy.Config.signature_apk_path
import com.pgyer.groovy.Config.walle_channel_config
import com.pgyer.groovy.Config.walle_jar_path
import com.pgyer.groovy.Config.zipalign_apk_path
import com.pgyer.groovy.NotificationUtils.sendMsgToDing
import com.pgyer.groovy.NotificationUtils.sendMsgToFeishu
import com.pgyer.groovy.NotificationUtils.sendMsgToWeChat
import com.pgyer.groovy.PgyerUtils.uploaPgyerAPK
import com.pgyer.groovy.PublishUtils.buildChannelApks
import com.pgyer.groovy.PublishUtils.reinforce
import com.pgyer.groovy.PublishUtils.signApkV2
import com.pgyer.groovy.PublishUtils.zipAlignApk
import com.pgyer.groovy.Utils.findApkFile
import com.pgyer.groovy.Utils.getFileSize
import com.pgyer.groovy.bean.NotificationChannel
import java.io.File

class PushUtils {
    /**
     * 上传蒲公英，发送消息
     */
    fun doTask(rootDir: File, msg: String, apkFile: File) {
        val responseData = uploaPgyerAPK(msg, apkFile)
        responseData?.let {
            when(Config.NOTIFICATION_CHANNEL){
                is NotificationChannel.Ding -> {
                    sendMsgToDing(rootDir, getFileSize(apkFile), it)
                }
                is NotificationChannel.FeiShu -> {
                    sendMsgToFeishu(rootDir, getFileSize(apkFile), it)
                }
                is NotificationChannel.WeChat ->{
                    sendMsgToWeChat(rootDir, getFileSize(apkFile), it)
                }
            }
        }
    }



    /**
     * 发布包：上传蒲公英、发送通知(钉钉、企业微信)、加固(乐固)、签名(V2)、多渠道打包(walle)
     */
    fun doPublishTask(rootDir: File, msg: String, apkFile: File){
        val responseData = uploaPgyerAPK(msg, apkFile)
        responseData?.let {
            when(Config.NOTIFICATION_CHANNEL){
                is NotificationChannel.Ding -> {
                    sendMsgToDing(rootDir, getFileSize(apkFile), it)
                }
                is NotificationChannel.FeiShu -> {
                    sendMsgToFeishu(rootDir, getFileSize(apkFile), it)
                }
                is NotificationChannel.WeChat ->{
                    sendMsgToWeChat(rootDir, getFileSize(apkFile), it)
                }
            }
        }
        val reinforceApk =reinforce("${rootDir}${legu_jar_path}",legu_app_id,legu_app_key,"${rootDir}${release_apk_path}${apkFile.name}", "${rootDir}${legu_apk_path}")
        println("========== 加固APK完成 =========")
        val zipalignApk= zipAlignApk(reinforceApk,"${rootDir}${zipalign_apk_path}")
        println("========== 对齐APK完成 =========")
        val signatureApk= signApkV2("${rootDir}${sign_jar_path}","${rootDir}${keystore_file_path}",keystore_alias,keystore_password,keystore_alias_password,zipalignApk,"${rootDir}${signature_apk_path}")
        println("========== 签名APK完成 =========")
        buildChannelApks("${rootDir}${walle_jar_path}","${rootDir}${walle_channel_config}",signatureApk,"${rootDir}${channel_apk_path}")
        println("========== 渠道APK完成 =========")
    }



}
