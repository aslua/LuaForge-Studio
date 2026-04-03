cmake_minimum_required(VERSION 3.31.6)

add_library(lfs SHARED
        lfs.c
)

target_link_libraries(lfs
        log
        lua
)

target_compile_options(lfs PRIVATE
        -DANDROID
        -Wall
)