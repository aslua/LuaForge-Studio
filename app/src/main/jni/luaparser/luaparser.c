/**
 * @file luaparser.c
 * @brief Lua C模块 - 封装luaD_protectedparser内部解析器接口
 * @note 修改为返回JSON格式结果
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "lua.h"
#include "lauxlib.h"
#include "lualib.h"
#include "ldo.h"
#include "lzio.h"
#include "lfunc.h"
#include "lstate.h"
#include "lgc.h"
#include "ltable.h"

/**
 * @brief 获取全局表宏(从lapi.c复制)
 */
#define getGtable(L)  \
    (&hvalue(&G(L)->l_registry)->array[LUA_RIDX_GLOBALS - 1])

/**
 * @brief 为闭包设置全局环境(_ENV)
 * @param L Lua状态机
 * 
 * 解析后的闭包第一个upvalue是_ENV，需要设置为全局表
 */
static void set_env(lua_State *L) {
    LClosure *f = clLvalue(s2v(L->top.p - 1));
    if (f->nupvalues >= 1) {
        const TValue *gt = getGtable(L);
        setobj(L, f->upvals[0]->v.p, gt);
        luaC_barrier(L, f->upvals[0], gt);
    }
}

/**
 * @brief 字符串读取器的用户数据结构
 */
typedef struct StringReaderData {
    const char *str;    /**< 字符串指针 */
    size_t size;        /**< 剩余大小 */
} StringReaderData;

/**
 * @brief 字符串读取器回调函数
 * @param L Lua状态机
 * @param ud 用户数据(StringReaderData*)
 * @param size [out]读取的字节数
 * @return 读取的数据指针，NULL表示结束
 */
static const char *string_reader(lua_State *L, void *ud, size_t *size) {
    StringReaderData *data = (StringReaderData *) ud;
    (void) L;
    if (data->size == 0) {
        *size = 0;
        return NULL;
    }
    *size = data->size;
    data->size = 0;
    return data->str;
}

/**
 * @brief 解析错误信息，提取行号、列号和错误描述
 * @param errmsg 错误信息字符串
 * @param line [out] 行号
 * @param column [out] 列号
 * @param message [out] 错误描述（缓冲区，需要预分配）
 * @param message_size 错误描述缓冲区大小
 */
static void
parse_error_info(const char *errmsg, int *line, int *column, char *message, size_t message_size) {
    // 默认值
    *line = 1;
    *column = 0;

    if (!errmsg) {
        snprintf(message, message_size, "Unknown error");
        return;
    }

    // 复制错误信息以便解析
    char *temp = strdup(errmsg);
    if (!temp) {
        snprintf(message, message_size, "Memory allocation error");
        return;
    }

    char *ptr = temp;

    // 1. 解析行号：从 "Line: " 中提取
    if (strstr(ptr, "Line:")) {
        ptr = strstr(ptr, "Line:") + strlen("Line:");
        while (*ptr && isspace((unsigned char) *ptr)) ptr++;

        if (*ptr) {
            *line = atoi(ptr);
        }
    }

    // 2. 解析列号：从 "luaparser:列号:" 中提取
    // 先找到 description 部分
    ptr = temp;
    char *description_start = NULL;

    if (strstr(ptr, "description:")) {
        description_start = strstr(ptr, "description:") + strlen("description:");
        while (*description_start && isspace((unsigned char) *description_start)) {
            description_start++;
        }
    }

    // 如果没有 description，使用整个字符串
    if (!description_start) {
        description_start = temp;
    }

    // 查找 luaparser:列号:
    if (strstr(description_start, "luaparser:")) {
        char *luaparser_start = strstr(description_start, "luaparser:") + strlen("luaparser:");

        // 提取列号
        *column = atoi(luaparser_start);

        // 查找第一个冒号之后的冒号
        char *first_colon = luaparser_start;
        while (*first_colon && *first_colon != ':') {
            first_colon++;
        }

        // 如果找到冒号，跳过它和后面的空格
        if (*first_colon == ':') {
            char *clean_message = first_colon + 1;
            while (*clean_message && isspace((unsigned char) *clean_message)) {
                clean_message++;
            }

            // 复制清理后的消息
            if (*clean_message) {
                snprintf(message, message_size, "%s", clean_message);

                // 清理 message 中的换行符
                for (char *p = message; *p; p++) {
                    if (*p == '\n' || *p == '\r') {
                        *p = ' ';
                    }
                }
            } else {
                snprintf(message, message_size, "%s", description_start);
            }
        } else {
            snprintf(message, message_size, "%s", description_start);
        }
    } else {
        // 没有找到 luaparser:，使用 description 或整个字符串
        snprintf(message, message_size, "%s", description_start);
    }

    // 如果 message 中包含 luaparser:列号:，再次清理
    char *luaparser_in_msg = strstr(message, "luaparser:");
    if (luaparser_in_msg) {
        // 找到第一个冒号
        char *first_colon = luaparser_in_msg + strlen("luaparser:");
        while (*first_colon && *first_colon != ':') {
            first_colon++;
        }

        if (*first_colon == ':') {
            // 提取列号（再次确认）
            *column = atoi(luaparser_in_msg + strlen("luaparser:"));

            // 移除 luaparser:列号: 部分
            char *clean_start = first_colon + 1;
            while (*clean_start && isspace((unsigned char) *clean_start)) {
                clean_start++;
            }

            if (*clean_start) {
                // 移动字符串到开头
                size_t len = strlen(clean_start);
                memmove(message, clean_start, len + 1);
            }
        }
    }

    free(temp);
}

/**
 * @brief JSON转义函数
 * @param input 输入字符串
 * @param output 输出缓冲区
 * @param out_size 输出缓冲区大小
 * @return 转义后的字符串
 */
static const char *json_escape(const char *input, char *output, size_t out_size) {
    if (!input || !output || out_size == 0) {
        return "";
    }

    char *p = output;
    size_t remaining = out_size - 1;

    while (*input != '\0' && remaining > 1) {
        switch (*input) {
            case '\"':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = '\"';
                remaining -= 2;
                break;
            case '\\':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = '\\';
                remaining -= 2;
                break;
            case '\b':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = 'b';
                remaining -= 2;
                break;
            case '\f':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = 'f';
                remaining -= 2;
                break;
            case '\n':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = 'n';
                remaining -= 2;
                break;
            case '\r':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = 'r';
                remaining -= 2;
                break;
            case '\t':
                if (remaining < 2) break;
                *p++ = '\\';
                *p++ = 't';
                remaining -= 2;
                break;
            default:
                *p++ = *input;
                remaining -= 1;
                break;
        }
        input++;
    }

    *p = '\0';
    return output;
}

/**
 * @brief 解析Lua代码字符串并返回JSON格式结果
 * @param L Lua状态机
 * @return 成功返回1(JSON字符串在栈顶)，失败返回错误信息JSON
 * 
 * Lua调用: luaparser.parse(code [, name [, mode]])
 *   - code: 要解析的Lua代码字符串
 *   - name: 代码块名称，默认"=luaparser"
 *   - mode: 加载模式 "t"(文本)/"b"(二进制)/"bt"(两者)，默认"bt"
 * 
 * 返回值格式:
 *   成功: {"status":true,"result":"Lua code parsed successfully","has_closure":true}
 *   失败: {"status":false,"line":行号,"column":列号,"message":"错误信息"}
 */
static int luaparser_parse(lua_State *L) {
    size_t len;
    const char *code = luaL_checklstring(L, 1, &len);
    const char *name = luaL_optstring(L, 2, "=luaparser");
    const char *mode = luaL_optstring(L, 3, "bt");

    StringReaderData data;
    data.str = code;
    data.size = len;

    ZIO z;
    luaZ_init(L, &z, string_reader, &data);

    // 保存当前栈顶，用于错误恢复
    int base = lua_gettop(L);

    int status = luaD_protectedparser(L, &z, name, mode);

    if (status != LUA_OK) {
        // 获取错误信息
        const char *errmsg = lua_tostring(L, -1);

        // 解析错误信息
        int line = 1;
        int column = 0;
        char message[1024];
        char escaped_message[2048];

        parse_error_info(errmsg, &line, &column, message, sizeof(message));
        json_escape(message, escaped_message, sizeof(escaped_message));

        // 生成JSON格式的错误信息
        char json_error[4096];
        snprintf(json_error, sizeof(json_error),
                 "{"
                 "\"status\":false,"
                 "\"line\":%d,"
                 "\"column\":%d,"
                 "\"message\":\"%s\""
                 "}",
                 line,
                 column,
                 escaped_message
        );

        // 清理栈，恢复状态
        lua_settop(L, base);

        // 压入JSON格式的错误信息
        lua_pushstring(L, json_error);
        return 1;
    }

    // 设置环境
    set_env(L);

    // 生成JSON格式的成功信息
    char json_success[1024];
    snprintf(json_success, sizeof(json_success),
             "{"
             "\"status\":true,"
             "\"result\":\"Lua code parsed successfully\","
             "\"has_closure\":true"
             "}"
    );

    // 清理栈：移除闭包，保留JSON字符串
    lua_pop(L, 1); // 弹出闭包
    lua_pushstring(L, json_success);
    return 1;
}

/**
 * @brief 模块函数注册表
 */
static const luaL_Reg luaparser_funcs[] = {
        {"parse", luaparser_parse},
        {NULL, NULL}
};

/**
 * @brief 模块入口函数
 * @param L Lua状态机
 * @return 1(模块表)
 */
LUAMOD_API int luaopen_luaparser(lua_State *L) {
    luaL_newlib(L, luaparser_funcs);
    return 1;
}