### 二维码扫描
#### 介绍
- 基于zxing-cpp实现的
  - zxingcpp 为jni代码
  - core 为zxing-cpp的c++源码
  - zint 为zxing-cpp的引用库
#### 类文件介绍
- AnalyzeCallback为回调接口
- CaptureFragment为扫描二维码的fragment 只包含一个Camera的相机View 不包含任何UI，实现UI在父Activity中实现
- CodeUtils 为二维码工具类 ，包含解析二维码、生成二维码、控制手电筒的方法
- ZxingCpp 为Jni方法用于调用zxing-cpp进行二维码操作
#### 使用
##### 配置仓库地址
```
     maven {
            url = uri("https://packages.aliyun.com/67170abc9d3c82efe37b1fa5/maven/app-component")
            credentials {
                username = "66e10af9d2d35942dba517b5"
                password = "c87aM]VgtLmJ"
            }
        }
```
##### 配置依赖
- 基础依赖
```
    implementation("com.fangtian:ft-scan-code:1.0.1-alpha")
```
- 本项目基于camera采集画面进行识别，若要使用Camera对象实现打开手电筒功能需添加下面以来
```
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
```

##### 在Activity中使用
- 扫码功能
```
 val mFragment: CaptureFragment = CaptureFragment.getFragment()
        mFragment.setAnalyzeCallback(object : AnalyzeCallback{
            override fun onAnalyzeFailed() {
                //todo 识别失败
            }

            override fun onAnalyzeSuccess(var1: Bitmap?, var2: String?) {
                //todo 识别成功
            }

        })
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, mFragment)
            .commitAllowingStateLoss()
```
- 打开手电筒
```
       binding.ivScanFlashLight.setOnClickListener {
            if (!isOpen) {
                CodeUtils.toggleLight(mFragment.getCamera(), true)
                isOpen = true
                binding.ivScanFlashLight.setImageResource(R.mipmap.flash_light_open)
            } else {
                CodeUtils.toggleLight(mFragment.getCamera(), false)
                isOpen = false
                binding.ivScanFlashLight.setImageResource(R.mipmap.flash_light_close)
            }
        }
```
- 识别二维码
```
 CodeUtils.analyzeBitmap(mBitmap,object : AnalyzeCallback{
            override fun onAnalyzeFailed() {
                //todo 识别失败
            }

            override fun onAnalyzeSuccess(var1: Bitmap?, var2: String?) {
                //todo 识别成功
            })
```
