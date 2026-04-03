cmake_minimum_required(VERSION 3.31.6)

add_library(gl SHARED
        luagl.c
        luagl_const.c
        luagl_util.c
)

target_include_directories(gl PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
)

target_compile_options(gl PRIVATE
        -DANDROID_NDK
        -DDISABLE_IMPORTGL
        -Wall
)

target_link_libraries(gl
        GLESv1_CM
        dl
        log
        lua
)