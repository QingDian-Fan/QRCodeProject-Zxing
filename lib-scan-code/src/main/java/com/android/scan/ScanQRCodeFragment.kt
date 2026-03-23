package com.android.scan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.fangtian.scan.databinding.FragmentScanQrCodeBinding
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ScanQRCodeFragment : Fragment() {
    companion object {
        @JvmStatic
        fun getFragment(intervalTime: Long = 5000) = ScanQRCodeFragment().apply {
            arguments = bundleOf("intervalTime" to intervalTime)
        }
    }

    private var camera: Camera? = null
    private var binding: FragmentScanQrCodeBinding? = null
    private var lastResultText: String? = null
    private var lastResultTime: Long = 0L
    private var intervalTime: Long = 5000L

    private val workService = Executors.newSingleThreadExecutor()

    private val readerOptions = NativeZxing.ReaderOptions(
        tryHarder = true,
        tryRotate = true,
        tryInvert = true,
        maxNumberOfSymbols = 1,
        textMode = NativeZxing.TextMode.PLAIN
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentScanQrCodeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intervalTime = arguments?.getLong("intervalTime") ?: 5000L
        startCameraX()
    }

    private fun startCameraX() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 预览流
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding?.previewView?.surfaceProvider)
            }

            // 图像分析流
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                // 建议设置分辨率提升识别率，但注意性能
                // .setTargetResolution(Size(1280, 720))
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("SCAN", "CameraX 绑定失败", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        workService.execute {
            val plane = imageProxy.planes[0]
            val results = NativeZxing.readYBuffer(
                plane.buffer,
                plane.rowStride,
                imageProxy.cropRect,
                imageProxy.imageInfo.rotationDegrees,
                readerOptions
            )

            if (!results.isNullOrEmpty()) {
                // --- 核心步骤：识别成功后获取 Bitmap ---
                val successBitmap = imageProxy.toBitmap()
                activity?.runOnUiThread {
                    // 回调给 Activity，带上 Bitmap
                    val resultText = results.first().text
                    val currentTime = System.currentTimeMillis()
                    if (resultText != lastResultText || (currentTime - lastResultTime) > intervalTime) {
                        lastResultText = resultText
                        lastResultTime = currentTime
                        analyzeCallback?.onAnalyzeSuccess(successBitmap, results.first().text)
                    }

                }
            }
            imageProxy.close()
        }
    }


    fun getCamera(): Camera? {
        return camera
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private var analyzeCallback: AnalyzeCallback? = null


    fun getAnalyzeCallback(): AnalyzeCallback? {
        return this.analyzeCallback
    }

    fun setAnalyzeCallback(analyzeCallback: AnalyzeCallback?) {
        this.analyzeCallback = analyzeCallback
    }
}