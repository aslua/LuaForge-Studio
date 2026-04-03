cmake_minimum_required(VERSION 3.31.6)

add_library(cjson SHARED
        lua_cjson.c
        strbuf.c
        # dtoa.c
        fpconv.c
        # g_fmt.c
)

target_link_libraries(cjson
        log
        lua
        m
)

target_compile_options(cjson PRIVATE
        -DANDROID
        -Wall
        -DMULTIPLE_THREADS
)
