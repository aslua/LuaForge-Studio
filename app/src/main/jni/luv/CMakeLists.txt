cmake_minimum_required(VERSION 3.31.6)

# 源文件列表（保持不变）
set(LIBUV_SRCS
        uv/unix/android-ifaddrs.c
        uv/unix/async.c
        uv/unix/core.c
        uv/unix/dl.c
        uv/unix/fs.c
        uv/unix/getaddrinfo.c
        uv/unix/getnameinfo.c
        uv/unix/linux-core.c
        uv/unix/linux-inotify.c
        uv/unix/linux-syscalls.c
        uv/unix/loop.c
        uv/unix/loop-watcher.c
        uv/unix/pipe.c
        uv/unix/poll.c
        uv/unix/process.c
        uv/unix/procfs-exepath.c
        uv/unix/proctitle.c
        uv/unix/pthread-fixes.c
        uv/unix/signal.c
        uv/unix/stream.c
        uv/unix/sysinfo-loadavg.c
        uv/unix/sysinfo-memory.c
        uv/unix/tcp.c
        uv/unix/thread.c
        uv/unix/tty.c
        uv/unix/udp.c
        uv/fs-poll.c
        uv/inet.c
        uv/threadpool.c
        uv/timer.c
        uv/uv-common.c
        uv/uv-data-getter-setters.c
        uv/version.c
)

set(LUV_SRCS
        luv/luv.c
)

add_library(luv SHARED
        ${LUV_SRCS}
        ${LIBUV_SRCS}
)

target_link_libraries(luv
        lua          # Lua 静态库，依赖 luajava 的符号
        luajava      # 提供 isJavaObject 符号
        log
        dl
        m
)

target_include_directories(luv PRIVATE
        ${CMAKE_SOURCE_DIR}/lua
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${CMAKE_CURRENT_SOURCE_DIR}/uv
        ${CMAKE_CURRENT_SOURCE_DIR}/luv
        ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_options(luv PRIVATE
        -fPIC
        -Wall
)

target_compile_definitions(luv PRIVATE
        ANDROID
        _GNU_SOURCE
)