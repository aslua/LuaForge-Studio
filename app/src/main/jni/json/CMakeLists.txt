cmake_minimum_required(VERSION 3.31.6)

add_library(json SHARED
        json-decode.c
        json-encode.c
        json.c
)

target_link_libraries(json
        log
        lua
)

target_compile_options(json PRIVATE
        -DANDROID
        -Wall
)
