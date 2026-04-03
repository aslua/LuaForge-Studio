cmake_minimum_required(VERSION 3.31.6)

# 源文件列表
set(UNIX_SRCS
        unix.c
        unix-getopt.c
)

# 创建共享库
add_library(unix SHARED ${UNIX_SRCS})

# 链接必要的库
target_link_libraries(unix
        lua
        log
        dl
)

# 包含目录
target_include_directories(unix PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 编译选项
target_compile_options(unix PRIVATE
        -fPIC
        -Wall
        -fvisibility=default
        -UHAVE_SIGTIMEDWAIT
        -UHAVE_GETIFADDRS
        -UHAVE_GETGRGID_R
        -UHAVE_GETGRNAM_R
        -UHAVE_FMEMOPEN
        -UHAVE_LOCKF
        -UHAVE_MKFIFOAT
        -UHAVE_PROGRAM_INVOCATION_SHORT_NAME
        -UHAVE_STRUCT_STAT_ST_ATIMESPEC
        -UHAVE_STRUCT_STAT_ST_MTIMESPEC
        -UHAVE_STRUCT_STAT_ST_CTIMESPEC
        -UHAVE_STRUCT_STAT_ST_ATIM
        -UHAVE_STRUCT_STAT_ST_MTIM
        -UHAVE_STRUCT_STAT_ST_CTIM
)

# 编译定义
target_compile_definitions(unix PRIVATE
        ANDROID
)