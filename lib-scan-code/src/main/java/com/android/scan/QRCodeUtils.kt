package com.android.scan

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.camera.core.Camera
import java.util.concurrent.Executors


object QRCodeUtils {
    private val workService = Executors.newSingleThreadExecutor()
    /*  fun analyzeQRCode(mBitmap: Bitmap, analyzeCallback: AnalyzeCallback?) {
          workService.execute {
              // 使用 ZxingCpp 解析
              val results = NativeZxing.readBitmap(
                  mBitmap,
                  Rect(0, 0, mBitmap.width, mBitmap.height),
                  0,
                  NativeZxing.ReaderOptions(tryHarder = true, tryInvert = true)
              )
              mHandler.post {
                  if (!results.isNullOrEmpty()) {
                      analyzeCallback?.onAnalyzeSuccess(mBitmap, results.first().text)
                  } else {
                      analyzeCallback?.onAnalyzeFailed()
                  }
              }
          }

      }*/

    fun analyzeQRCode(
        bitmap: Bitmap,
        callback: AnalyzeCallback?,
    ) {
        workService.execute {
            val options = NativeZxing.ReaderOptions().apply {
                tryHarder = true
                tryRotate = true
                tryInvert = true
                tryDownscale = false
                minLineCount = 1
            }

            var results = NativeZxing.readBitmap(
                bitmap,
                Rect(0, 0, bitmap.width, bitmap.height),
                0,
                options
            )

            if (!results.isNullOrEmpty()) {
                callback?.onAnalyzeSuccess(bitmap, results.first().text)
                return@execute
            }

            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                bitmap.width * 2,
                bitmap.height * 2,
                true
            )

            results = NativeZxing.readBitmap(
                scaled,
                Rect(0, 0, scaled.width, scaled.height),
                0,
                options
            )

            if (!results.isNullOrEmpty()) {
                callback?.onAnalyzeSuccess(bitmap, results.first().text)
                return@execute
            }

            options.binarizer = NativeZxing.Binarizer.GLOBAL_HISTOGRAM

            results = NativeZxing.readBitmap(
                bitmap,
                Rect(0, 0, bitmap.width, bitmap.height),
                0,
                options
            )

            if (!results.isNullOrEmpty()) {
                callback?.onAnalyzeSuccess(bitmap, results.first().text)
                return@execute
            }

            callback?.onAnalyzeFailed()
        }
    }

    /**
     * 生成带 Logo 的二维码
     */
    fun generateQRCode(text: String, w: Int, h: Int, logo: Bitmap?): Bitmap? {
        if (TextUtils.isEmpty(text)) return null

        return try {
            // 使用 ZxingCpp 生成 BitMatrix
            val bitMatrix = NativeZxing.encodeStringJava(
                text = text,
                format = NativeZxing.BarcodeFormat.QRCode.toString(),
                width = w,
                height = h,
                margin = 0
            )

            val pixels = IntArray(w * h)
            val scaleLogo = getScaleLogo(logo, w, h)

            var offsetX = 0
            var offsetY = 0
            if (scaleLogo != null) {
                offsetX = (w - scaleLogo.width) / 2
                offsetY = (h - scaleLogo.height) / 2
            }

            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (scaleLogo != null && x >= offsetX && x < offsetX + scaleLogo.width &&
                        y >= offsetY && y < offsetY + scaleLogo.height
                    ) {
                        // 绘制 Logo
                        val pixel = scaleLogo.getPixel(x - offsetX, y - offsetY)
                        if (pixel == 0) { // 如果 logo 像素透明，则显示二维码背景
                            pixels[y * w + x] = if (bitMatrix.get(x, y)) -0x1000000 else -0x1
                        } else {
                            pixels[y * w + x] = pixel
                        }
                    } else {
                        // 绘制二维码点位
                        pixels[y * w + x] = if (bitMatrix.get(x, y)) -0x1000000 else -0x1
                    }
                }
            }

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getScaleLogo(logo: Bitmap?, w: Int, h: Int): Bitmap? {
        if (logo == null) return null
        val matrix = Matrix()
        val scaleFactor = (w * 1.0f / 5.0f / logo.width).coerceAtMost(h * 1.0f / 5.0f / logo.height)
        matrix.postScale(scaleFactor, scaleFactor)
        return Bitmap.createBitmap(logo, 0, 0, logo.width, logo.height, matrix, true)
    }

    /**
     * 控制手电筒 (适配 CameraX)
     * 需要在 Fragment 中获取 camera 对象并传入
     */
    fun toggleLight(camera: Camera?, isEnable: Boolean) {
        camera?.cameraControl?.enableTorch(isEnable)
    }


}