cmake_minimum_required(VERSION 3.31.6)

# 设置源文件
set(BASE64_SRCS
        base64.c
        lua_base64.c
)

# 创建共享库（Lua 模块）
add_library(base64 SHARED
        ${BASE64_SRCS}
)

# 链接 Lua 库（必须链接 lua 库才能调用 Lua API）
target_link_libraries(base64
        lua
        log
)

# 包含目录
target_include_directories(base64 PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 添加编译选项
target_compile_options(base64 PRIVATE
        -fPIC
        -Wall
        -Werror
        -fvisibility=default  # 确保符号可见
)