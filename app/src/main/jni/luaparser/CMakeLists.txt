cmake_minimum_required(VERSION 3.31.6)

# 设置 JNI 包含路径
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../lua)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../luajava)  # 添加 luajava 头文件
include_directories($CMAKE_SYSROOT/usr/include/jni)
include_directories($CMAKE_SYSROOT/usr/include)

# 查找 JNI 包
find_package(JNI REQUIRED)
if (JNI_FOUND)
    message(STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message(STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
    include_directories(${JNI_INCLUDE_DIRS})
endif ()

# 创建共享库（JNI）
add_library(luaparser SHARED
        luaparser_jni.c
)

# 链接 lua、luajava 静态库和 log 库
target_link_libraries(luaparser
        lua
        luajava
        log
        ${JNI_LIBRARIES}
)

# 包含 lua 头文件目录
target_include_directories(luaparser PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/../lua
        ${CMAKE_CURRENT_SOURCE_DIR}/../luajava
)

# 编译选项
target_compile_options(luaparser PRIVATE
        -std=c11
        -O2
        -fPIC
        -DANDROID
        -Wall
        -Wno-unused-parameter    # 允许未使用的参数
        -Wno-unused-function     # 允许未使用的函数
        -Wno-unused-variable     # 允许未使用的变量
)

# 链接器选项
target_link_options(luaparser PRIVATE
        -Wl,--build-id=sha1
        -Wl,--no-undefined
        -Wl,-z,noexecstack
        -Wl,-z,relro
        -Wl,-z,now
)