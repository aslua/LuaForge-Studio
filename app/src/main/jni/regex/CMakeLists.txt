cmake_minimum_required(VERSION 3.31.6)

set(REGEX_SRCS
        lposix.c
        common.c
)

add_library(regex SHARED ${REGEX_SRCS})

target_link_libraries(regex
        lua
        log
        dl
)

target_include_directories(regex PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_options(regex PRIVATE
        -fPIC
        -Wall
        -fvisibility=default
)