package com.android.scan

import android.graphics.Bitmap

interface AnalyzeCallback {
    fun onAnalyzeSuccess(var1: Bitmap?, var2: String?)

    fun onAnalyzeFailed()
}
