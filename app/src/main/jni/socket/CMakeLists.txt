cmake_minimum_required(VERSION 3.31.6)

set(SOCKET_SRCS
        auxiliar.c
        buffer.c
        compat.c
        except.c
        inet.c
        io.c
        luasocket.c
        mime.c
        options.c
        select.c
        serial.c
        tcp.c
        timeout.c
        udp.c
        unix.c
        unixdgram.c
        unixstream.c
        usocket.c
)

# Windows 平台文件（如果需要）
# list(APPEND SOCKET_SRCS wsocket.c)

add_library(socket SHARED
        ${SOCKET_SRCS}
)

target_compile_definitions(socket PRIVATE
        "LUASOCKET_API=__attribute__((visibility(\"default\")))"
        "UNIX_API=__attribute__((visibility(\"default\")))"
        "MIME_API=__attribute__((visibility(\"default\")))"
)

target_link_libraries(socket
        log
        lua
)

target_compile_options(socket PRIVATE
        -DANDROID
        -Wall
)

# 单独的 libmime.so
add_library(mime SHARED
        mime.c
)

target_compile_definitions(mime PRIVATE
        "MIME_API=__attribute__((visibility(\"default\")))"
)

target_link_libraries(mime
        log
        lua
)

target_compile_options(mime PRIVATE
        -DANDROID
        -Wall
)
