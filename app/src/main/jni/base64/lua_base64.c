#include <lua.h>
#include <lauxlib.h>
#include <lualib.h>
#include <stdlib.h>
#include "base64.h"

// Lua 函数：base64.encode(str)
static int l_base64_encode(lua_State *L) {
    size_t len;
    const char *str = luaL_checklstring(L, 1, &len);

    // 计算输出缓冲区大小
    unsigned int out_len = BASE64_ENCODE_OUT_SIZE(len);
    char *out = (char *) malloc(out_len);
    if (!out) {
        luaL_error(L, "memory allocation failed");
    }

    // 编码
    unsigned int result_len = base64_encode((const unsigned char *) str, len, out);

    // 返回结果
    lua_pushlstring(L, out, result_len);
    free(out);
    return 1;
}

// Lua 函数：base64.decode(str)
static int l_base64_decode(lua_State *L) {
    size_t len;
    const char *str = luaL_checklstring(L, 1, &len);

    // 计算输出缓冲区大小
    unsigned int out_len = BASE64_DECODE_OUT_SIZE(len);
    unsigned char *out = (unsigned char *) malloc(out_len);
    if (!out) {
        luaL_error(L, "memory allocation failed");
    }

    // 解码
    unsigned int result_len = base64_decode(str, len, out);
    if (result_len == 0) {
        free(out);
        luaL_error(L, "invalid base64 string");
    }

    // 返回结果
    lua_pushlstring(L, (const char *) out, result_len);
    free(out);
    return 1;
}

// 模块函数表
static const luaL_Reg base64_lib[] = {
        {"encode", l_base64_encode},
        {"decode", l_base64_decode},
        {NULL, NULL}
};

// 模块入口函数（必须导出为 C 符号）
LUAMOD_API int luaopen_base64(lua_State *L) {
    // 创建模块表
    luaL_newlib(L, base64_lib);
    return 1;
}