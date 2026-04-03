cmake_minimum_required(VERSION 3.31.6)

add_library(yyjson SHARED
        lua_yyjson.c
        yyjson.c
)

# 链接 log 和 lua
target_link_libraries(yyjson
        log
        lua
)