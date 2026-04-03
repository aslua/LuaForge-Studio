cmake_minimum_required(VERSION 3.31.6)

set(SENSOR_SRCS
        luasensor.cpp
)

add_library(sensor SHARED ${SENSOR_SRCS})

target_link_libraries(sensor
        lua
        android
        log
)

# 包含目录
target_include_directories(sensor PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 编译选项
target_compile_options(sensor PRIVATE
        -fPIC
        -Wall
        -fvisibility=default
)