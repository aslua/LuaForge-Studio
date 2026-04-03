cmake_minimum_required(VERSION 3.31.6)

set(MICRO_AES_SRCS
        micro_aes.c
        lua_micro_aes.c
)

add_library(microaes SHARED ${MICRO_AES_SRCS})

# 链接必要的库
target_link_libraries(microaes
        lua
        log
        m
)

# 包含目录
target_include_directories(microaes PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_SOURCE_DIR}/lua
)

# 编译选项
target_compile_options(microaes PRIVATE
        -fPIC
        -Wall
        -Werror
        -fvisibility=default
)

target_compile_definitions(microaes PRIVATE AES___=256)