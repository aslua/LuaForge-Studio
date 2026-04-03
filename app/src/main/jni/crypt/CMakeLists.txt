cmake_minimum_required(VERSION 3.31.6)

add_library(crypt SHARED
        lcrypt.c
        lsha1.c
)

# 关键：添加当前目录到包含路径
target_include_directories(crypt PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}  # 使 #include "lcrypt.h" 能找到
        ${CMAKE_SOURCE_DIR}/lua
)

target_link_libraries(crypt
        log
        lua
)

target_compile_options(crypt PRIVATE
        -DANDROID
        -Wall
)