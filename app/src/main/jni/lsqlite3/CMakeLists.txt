cmake_minimum_required(VERSION 3.31.6)

add_library(lsqlite3 SHARED
        lsqlite3.c
        sqlite3.c
)

target_link_libraries(lsqlite3
        log
        lua
        dl
)

target_compile_options(lsqlite3 PRIVATE
        -DANDROID
        -Wall
        -DSQLITE_ENABLE_FTS3
        -DSQLITE_ENABLE_FTS3_PARENTHESIS
        -DSQLITE_ENABLE_FTS4
        -DSQLITE_ENABLE_FTS5
        -DSQLITE_ENABLE_JSON1
        -DSQLITE_ENABLE_RTREE
)