cmake_minimum_required(VERSION 3.31.6)

add_library(luajava SHARED
        luajava.c
        decode.c
)

# 链接 log 和 lua
target_link_libraries(luajava
        log
        lua
)

target_compile_options(luajava PRIVATE
        -DANDROID
        -Wall
        -DJNI_VERSION_1_6
)
