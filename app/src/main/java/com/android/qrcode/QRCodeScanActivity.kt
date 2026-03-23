package com.android.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.android.qrcode.databinding.ActivityQrCodeScanBinding
import com.android.scan.AnalyzeCallback
import com.android.scan.ScanQRCodeFragment
import com.android.scan.QRCodeUtils


class QRCodeScanActivity : AppCompatActivity() {
    var isOpen: Boolean = false

    private lateinit var binding: ActivityQrCodeScanBinding
    private  val REQ_CODE_SELECT_PIC = 3

    private val callback = object : AnalyzeCallback {
        override fun onAnalyzeSuccess(
            var1: Bitmap?,
            var2: String?,
        ) {
            mHandler.post {
                Toast.makeText(this@QRCodeScanActivity,"解析结果：$var2", Toast.LENGTH_LONG).show()
            }
        }

        override fun onAnalyzeFailed() {
            mHandler.post {
                Toast.makeText(this@QRCodeScanActivity,"解析失败", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val mHandler = Handler(Looper.getMainLooper())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SELECT_PIC -> {
                PictureSelector.result(resultCode, data)?.let {
                    val mBitmap =  getBitmapFromUri(this@QRCodeScanActivity,it)
                    mBitmap?.let {mBitmap->
                        QRCodeUtils.analyzeQRCode(mBitmap,callback)
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()
        val mFragment: ScanQRCodeFragment = ScanQRCodeFragment.getFragment()
        mFragment.setAnalyzeCallback(callback)
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, mFragment)
            .commitAllowingStateLoss()


        binding.ivScanFlashLight.setOnClickListener {
            if (!isOpen) {
                QRCodeUtils.toggleLight(mFragment.getCamera(), true)
                isOpen = true
                binding.ivScanFlashLight.setImageResource(R.mipmap.flash_light_open)
            } else {
                QRCodeUtils.toggleLight(mFragment.getCamera(), false)
                isOpen = false
                binding.ivScanFlashLight.setImageResource(R.mipmap.flash_light_close)
            }
        }

        binding.ivScanSelectImg.setOnClickListener {
            PictureSelector.select(this@QRCodeScanActivity, REQ_CODE_SELECT_PIC)
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}