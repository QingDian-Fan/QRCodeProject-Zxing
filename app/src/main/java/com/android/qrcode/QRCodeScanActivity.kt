package com.android.qrcode

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.qrcode.databinding.ActivityQrCodeScanBinding
import com.android.scan.AnalyzeCallback
import com.android.scan.ScanQRCodeFragment
import com.android.scan.QRCodeUtils


class QRCodeScanActivity : AppCompatActivity() {
    var isOpen: Boolean = false

    private lateinit var binding: ActivityQrCodeScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()
        val mFragment: ScanQRCodeFragment = ScanQRCodeFragment.getFragment()
        mFragment.setAnalyzeCallback(object : AnalyzeCallback {
            override fun onAnalyzeFailed() {

            }

            override fun onAnalyzeSuccess(var1: Bitmap?, var2: String?) {

            }

        })
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
    }


}