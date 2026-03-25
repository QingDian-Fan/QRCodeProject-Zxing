package com.pgyer.groovy

import com.pgyer.groovy.NotificationUtils.sendMsgToDing
import com.pgyer.groovy.PgyerUtils.uploaPgyerAPK
import com.pgyer.groovy.Utils.getFileSize
import java.io.File

class PushUtils {
    /**
     * 上传蒲公英，发送消息
     */
    fun doTask(rootDir: File, msg: String, apkFile: File) {
        val responseData = uploaPgyerAPK(msg, apkFile)
        responseData?.let {
            sendMsgToDing(rootDir, getFileSize(apkFile), it)
        }
    }



}
