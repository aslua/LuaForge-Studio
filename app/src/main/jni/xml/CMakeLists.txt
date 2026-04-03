cmake_minimum_required(VERSION 3.31.6)

# 源文件列表
set(XML_SRCS
        xml.c
)

# 创建共享库
add_library(xml SHARED ${XML_SRCS})

# 链接必要的库
target_link_libraries(xml
        lua
        log
        dl
)

# 包含目录
target_include_directories(xml PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 编译选项
target_compile_options(xml PRIVATE
        -fPIC
        -Wall
        -fvisibility=default
)