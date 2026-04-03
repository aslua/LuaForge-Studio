#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>

/**
 * @brief 读取文件内容（字符串形式）
 * @param L Lua状态机
 * @return 返回读取的文件内容字符串或nil
 */
static int rawio_file_read(lua_State *L) {
    const char *filename = luaL_checkstring(L, 1);
    const char *modes = luaL_optstring(L, 2, "rb");

    // 加强参数验证
    if (!filename || strlen(filename) == 0) {
        luaL_argerror(L, 1, "文件名不能为空");
        return 0;
    }

    FILE *fp = fopen(filename, modes);
    if (!fp) {
        lua_pushnil(L);
        return 1;
    }

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);
    rewind(fp);

    // 处理空文件
    if (size < 0) {
        fclose(fp);
        lua_pushnil(L);
        return 1;
    }

    char *buffer = (char *) malloc(size + 1);
    if (!buffer) {
        fclose(fp);
        lua_pushnil(L);
        return 1;
    }

    size_t read_size = fread(buffer, 1, size, fp);
    buffer[read_size] = '\0';

    fclose(fp);
    lua_pushstring(L, buffer);
    free(buffer);

    return 1;
}

/**
 * @brief 读取文件内容（二进制形式）
 * @param L Lua状态机
 * @return 返回读取的二进制数据或nil
 */
static int rawio_file_tsread(lua_State *L) {
    const char *filename = luaL_checkstring(L, 1);
    const char *modes = luaL_optstring(L, 2, "rb");

    // 加强参数验证
    if (!filename || strlen(filename) == 0) {
        luaL_argerror(L, 1, "文件名不能为空");
        return 0;
    }

    FILE *fp = fopen(filename, modes);
    if (!fp) {
        lua_pushnil(L);
        return 1;
    }

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);
    rewind(fp);

    // 处理空文件
    if (size < 0) {
        fclose(fp);
        lua_pushnil(L);
        return 1;
    }

    void *buffer = malloc(size);
    if (!buffer) {
        fclose(fp);
        lua_pushnil(L);
        return 1;
    }

    size_t read_size = fread(buffer, 1, size, fp);
    fclose(fp);

    lua_pushlstring(L, buffer, read_size);
    free(buffer);

    return 1;
}

/**
 * @brief 递归创建目录
 * @param path 目录路径
 * @return 成功返回0，失败返回-1
 */
static int rawio_mkdir_recursive(const char *path) {
    char *dir = strdup(path);
    char *p = NULL;
    int result = 0;

    // 跳过根目录
    for (p = dir + 1; *p; p++) {
        if (*p == '/') {
            *p = '\0';
            if (access(dir, F_OK) != 0) {
                if (mkdir(dir, 0755) != 0) {
                    result = -1;
                    break;
                }
            }
            *p = '/';
        }
    }

    // 创建最后一级目录
    if (result == 0 && access(dir, F_OK) != 0) {
        if (mkdir(dir, 0755) != 0) {
            result = -1;
        }
    }

    free(dir);
    return result;
}

/**
 * @brief 写入文件内容
 * @param L Lua状态机
 * @return 成功返回true，失败返回false和错误信息
 */
static int rawio_file_write(lua_State *L) {
    const char *filename = luaL_checkstring(L, 1);
    size_t content_len;
    const char *content = luaL_checklstring(L, 2, &content_len);
    int create_dir = lua_toboolean(L, 3);
    int throw_error = lua_toboolean(L, 4);

    // 加强参数验证
    if (!filename || strlen(filename) == 0) {
        if (throw_error) {
            return luaL_error(L, "文件名不能为空");
        } else {
            lua_pushstring(L, "文件名不能为空");
            return 1;
        }
    }

    // 如果需要创建目录
    if (create_dir) {
        char *dir = strrchr(filename, '/');
        if (dir) {
            char *path = strdup(filename);
            path[dir - filename] = '\0';

            if (rawio_mkdir_recursive(path) != 0) {
                free(path);
                if (throw_error) {
                    return luaL_error(L, "创建目录失败");
                } else {
                    lua_pushstring(L, "创建目录失败");
                    return 1;
                }
            }
            free(path);
        }
    }

    // 打开文件进行写入
    FILE *fp = fopen(filename, "wb");
    if (!fp) {
        if (throw_error) {
            return luaL_error(L, "打开文件失败");
        } else {
            lua_pushstring(L, "打开文件失败");
            return 1;
        }
    }

    // 写入内容
    size_t written = fwrite(content, 1, content_len, fp);

    // 确保内容被刷新到磁盘
    fflush(fp);
    fclose(fp);

    if (written != content_len) {
        if (throw_error) {
            return luaL_error(L, "写入文件失败");
        } else {
            lua_pushstring(L, "写入文件失败");
            return 1;
        }
    }

    lua_pushboolean(L, 1);
    return 1;
}

// 模块函数列表
static const luaL_Reg rawio_funcs[] = {
        {"read",        rawio_file_read},
        {"ioread",      rawio_file_read},
        {"file_read",   rawio_file_read},
        {"tsread",      rawio_file_tsread},
        {"iotsread",    rawio_file_tsread},
        {"file_tsread", rawio_file_tsread},
        {"write",       rawio_file_write},
        {"iowrite",     rawio_file_write},
        {"file_write",  rawio_file_write},
        {NULL, NULL}
};

/**
 * @brief 模块入口函数
 * @param L Lua状态机
 * @return 返回模块表
 */
LUALIB_API int luaopen_rawio(lua_State *L) {
    luaL_newlib(L, rawio_funcs);
    return 1;
}