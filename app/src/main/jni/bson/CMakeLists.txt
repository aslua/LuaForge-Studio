cmake_minimum_required(VERSION 3.31.6)

add_library(bson SHARED
        lua-bson.c
)

target_link_libraries(bson
        log
        lua
)

target_compile_options(bson PRIVATE
        -DANDROID
        -Wall
)
