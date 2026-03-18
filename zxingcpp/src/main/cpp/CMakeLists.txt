cmake_minimum_required(VERSION 3.16)
project(ZXingCppAndroid)

# 1️⃣ 设置 C++ 标准
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 2️⃣ 配置 ZXing 读写功能
set(ZXING_WRITERS ON)
set(ZXING_READERS ON)

# 3️⃣ 添加 core 库
# 这里必须指向包含 core CMakeLists.txt 的目录
add_subdirectory(
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../core  # <- 核对路径
        zxing_core_build                               # build 子目录，避免冲突
)

# 4️⃣ 添加你自己的 JNI 库
add_library(
        zxingcpp_android
        SHARED
        native-zxing.cpp
)

# 5️⃣ 添加头文件路径
target_include_directories(
        zxingcpp_android
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../core/src
)

# 6️⃣ 链接 ZXing 核心库和 jnigraphics
target_link_libraries(
        zxingcpp_android
        PRIVATE
        ZXing::ZXing   # core 库 target
        jnigraphics    # Android JNI 图形库
)

# 7️⃣ 可选：设置链接选项（优化页面大小）
target_link_options(zxingcpp_android PRIVATE "-Wl,-z,max-page-size=16384")

