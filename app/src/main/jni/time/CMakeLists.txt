cmake_minimum_required(VERSION 3.31.6)

# 源文件列表
set(TIME_SRCS
        time.c
        lua_time.c
)

# 创建共享库
add_library(time SHARED ${TIME_SRCS})

# 链接必要的库
target_link_libraries(time
        lua
        log
        dl
        z
)

# 包含目录
target_include_directories(time PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 编译选项
target_compile_options(time PRIVATE
        -fPIC
        -Wall
        -std=c17
        -funroll-loops
        -fomit-frame-pointer
        -fstrict-aliasing
        -fvisibility=default
)