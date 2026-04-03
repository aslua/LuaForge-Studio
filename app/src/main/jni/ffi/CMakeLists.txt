cmake_minimum_required(VERSION 3.31.6)

# 启用汇编语言支持（确保 .S 文件被编译）
enable_language(ASM)

# 模块名称
set(TARGET ffi)

# 公共源文件（与 ABI 无关）
set(SOURCES
        src/closures.c
        src/debug.c
        src/java_raw_api.c
        src/prep_cif.c
        src/raw_api.c
        src/types.c
        ast.cc
        ffi.cc
        ffilib.cc
        lib.cc
        lua.cc
        main.cc
        parser.cc
        util.cc
        byopen_android.c
)

# 公共头文件目录
set(INCLUDES
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${CMAKE_CURRENT_SOURCE_DIR}/../lua
)

message(STATUS "Building for ABI: ${CMAKE_ANDROID_ARCH_ABI}")

# 根据 ABI 添加架构相关源文件和头文件目录
if (CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    list(APPEND SOURCES src/arm/sysv.S src/arm/ffi.c)
    list(APPEND INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/linux-arm)
    message(STATUS "Added armeabi-v7a sources: sysv.S and ffi.c")
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    list(APPEND SOURCES src/aarch64/sysv.S src/aarch64/ffi.c)
    list(APPEND INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/linux-arm64)
    message(STATUS "Added arm64-v8a sources: sysv.S and ffi.c")
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "x86")
    list(APPEND SOURCES src/x86/sysv.S src/x86/ffi.c)
    list(APPEND INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/linux-x86)
    message(STATUS "Added x86 sources: sysv.S and ffi.c")
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "x86_64")
    list(APPEND SOURCES
            src/x86/ffi64.c
            src/x86/ffiw64.c
            src/x86/unix64.S
            src/x86/win64.S
    )
    list(APPEND INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/linux-x86)
    message(STATUS "Added x86_64 sources")
endif ()

# 创建共享库
add_library(${TARGET} SHARED ${SOURCES})

# 设置 C++ 标准为 C++14（代码需要）
set_target_properties(${TARGET} PROPERTIES
        CXX_STANDARD 14
        CXX_STANDARD_REQUIRED ON
        CXX_EXTENSIONS OFF
)

# 包含目录
target_include_directories(${TARGET} PRIVATE ${INCLUDES})

# 链接依赖库
target_link_libraries(${TARGET}
        lua          # Lua 静态库
        log          # Android 日志
        dl           # 动态链接
        c++_static
)

# 编译选项
target_compile_options(${TARGET} PRIVATE
        # C 文件使用 GNU99 标准（支持 asm 关键字）
        $<$<COMPILE_LANGUAGE:C>:-std=gnu99>
        # 其他通用选项
        -DFFI_LITTLE_ENDIAN
        -Wno-deprecated-declarations
        -fstrict-aliasing
        -ffunction-sections -fdata-sections
)

# 链接选项（优化）
target_link_options(${TARGET} PRIVATE
        -Wl,--gc-sections
        -Wl,--as-needed
        -Wl,--exclude-libs,ALL
)

# 导出 luaopen_ffi 符号供 Lua 动态加载
target_link_options(${TARGET} PRIVATE -Wl,--export-dynamic-symbol=luaopen_ffi)