#include <lua.h>
#include <lauxlib.h>
#include <lualib.h>
#include "time.h"

/**
 * Lua绑定：获取当前时间戳（毫秒）
 */
static int lua_getCurrentTimestamp(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) getCurrentTimestamp());
    return 1;
}

/**
 * Lua绑定：获取当前时间（秒）
 */
static int lua_getCurrentTime(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) getCurrentTime());
    return 1;
}

/**
 * Lua绑定：开始计时
 */
static int lua_startTime(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) startTime());
    return 1;
}

/**
 * Lua绑定：结束计时并返回经过的时间
 */
static int lua_endTime(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) endTime((Timestamp) luaL_checkinteger(L, 1)));
    return 1;
}

/**
 * Lua绑定：创建倒计时
 */
static int lua_createCountdown(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) createCountdown((int64_t) luaL_checkinteger(L, 1)));
    return 1;
}

/**
 * Lua绑定：检查倒计时是否结束
 */
static int lua_isCountdownEnded(lua_State *L) {
    lua_pushboolean(L, isCountdownEnded((Timestamp) luaL_checkinteger(L, 1)));
    return 1;
}

/**
 * Lua绑定：获取倒计时剩余时间
 */
static int lua_getCountdownRemaining(lua_State *L) {
    lua_pushinteger(L, (lua_Integer) getCountdownRemaining((Timestamp) luaL_checkinteger(L, 1)));
    return 1;
}

/**
 * Lua绑定：比较两个时间戳的差值
 */
static int lua_compareTimestamps(lua_State *L) {
    Timestamp t1 = (Timestamp) luaL_checkinteger(L, 1);
    Timestamp t2 = (Timestamp) luaL_checkinteger(L, 2);
    lua_pushinteger(L, (lua_Integer) compareTimestamps(t1, t2));
    return 1;
}

// 模块函数列表
static const luaL_Reg time_module[] = {
        {"now",       lua_getCurrentTimestamp},
        {"time",      lua_getCurrentTime},
        {"start",     lua_startTime},
        {"elapsed",   lua_endTime},
        {"countdown", lua_createCountdown},
        {"isEnded",   lua_isCountdownEnded},
        {"remaining", lua_getCountdownRemaining},
        {"diff",      lua_compareTimestamps},
        {NULL, NULL}
};

// 模块初始化函数
int luaopen_time(lua_State *L) {
    luaL_newlib(L, time_module);
    return 1;
}
