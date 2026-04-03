cmake_minimum_required(VERSION 3.31.6)

add_library(canvas SHARED
        canvas.c
)

target_link_libraries(canvas
        log
        lua
        GLESv2
)

target_compile_options(canvas PRIVATE
        -DANDROID
        -Wall
)
