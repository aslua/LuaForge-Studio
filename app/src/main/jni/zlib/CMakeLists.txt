cmake_minimum_required(VERSION 3.31.6)

# 源文件列表
set(ZLIB_SRCS
        lua_zlib.c
)

# 创建共享库
add_library(zlib SHARED ${ZLIB_SRCS})

# 链接必要的库
target_link_libraries(zlib
        lua
        z
)

# 包含目录
target_include_directories(zlib PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 编译选项
target_compile_options(zlib PRIVATE
        -fPIC
        -Wall
        -std=c17
        -funroll-loops
        -fomit-frame-pointer
        -fstrict-aliasing
        -fvisibility=default
)