cmake_minimum_required(VERSION 3.31.6)

set(TCC_SRCS
    tccpp.c
    libtcc.c
    tccelf.c
    tccasm.c
    tccgen.c
    tccrun.c
    arm-gen.c
    tcc_jni.c
)

add_library(tcc SHARED ${TCC_SRCS})

target_link_libraries(tcc
    lua
    log
)

target_include_directories(tcc PRIVATE
    ${CMAKE_SOURCE_DIR}/lua
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_options(tcc PRIVATE
    -DTCC_TARGET_ARM
    -DTCC_ARM_VFP
    -DTCC_TARGET_ARM
    -DTCC_ARM_EABI
    -std=c99
    -Wno-pointer-sign
)

target_compile_definitions(tcc PRIVATE
    ANDROID
)