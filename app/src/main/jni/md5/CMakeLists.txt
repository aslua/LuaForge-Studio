cmake_minimum_required(VERSION 3.31.6)

add_library(md5 SHARED
        md5lib.c
        md5.c
        compat-5.2.c
)

target_link_libraries(md5
        log
        lua
)

target_compile_options(md5 PRIVATE
        -DANDROID
        -Wall
)