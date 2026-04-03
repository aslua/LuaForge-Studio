cmake_minimum_required(VERSION 3.31.6)

# 源文件
set(RAWIO_SRCS
        rawio.c
)

# 创建共享库
add_library(rawio SHARED ${RAWIO_SRCS})

# 链接必要的库
target_link_libraries(rawio
        lua          # Lua 静态库
        log          # Android 日志库
        dl           # 动态链接器
        z            # zlib 压缩库
)

# 包含目录
target_include_directories(rawio PRIVATE
        ${CMAKE_SOURCE_DIR}/lua        # Lua 头文件
        ${CMAKE_CURRENT_SOURCE_DIR}     # 当前目录
)

# 编译选项（从 Android.mk 提取，优化级别和 LTO 依赖全局构建类型）
target_compile_options(rawio PRIVATE
        -fPIC                           # 共享库必需（add_library SHARED 自动添加，但显式注明无妨）
        -Wall                           # 开启常见警告
        -std=c17                        # 使用 C17 标准
        -funroll-loops                  # 循环展开优化
        -fomit-frame-pointer             # 省略帧指针
        -fstrict-aliasing                # 严格别名规则优化
        -fno-exceptions                  # 禁用 C++ 异常（C 代码中无影响）
        -fno-unwind-tables                # 不生成 unwind 表
        -fno-asynchronous-unwind-tables   # 不生成异步 unwind 表
        -fvisibility=default              # 确保符号可见（配合 luaopen_rawio 导出）
)

# 链接选项（全局已包含 -Wl,--gc-sections 等，此处仅保留模块特有选项）
target_link_options(rawio PRIVATE
        -Wl,--gc-sections                # 垃圾回收未使用的段（全局已设置，保留无妨）
)