/*
** $Id: llex.h $
** Lexical Analyzer
** See Copyright Notice in lua.h
*/

#ifndef llex_h
#define llex_h

#include <limits.h>

#include "lobject.h"
#include "lzio.h"


/*
** Single-char tokens (terminal symbols) are represented by their own
** numeric code. Other tokens start at the following value.
*/
#define FIRST_RESERVED    (UCHAR_MAX + 1)


#if !defined(LUA_ENV)
#define LUA_ENV        "_ENV"
#endif


/*
* WARNING: if you change the order of this enumeration,
* grep "ORDER RESERVED"
*/
enum RESERVED {
    TK_AND = FIRST_RESERVED, TK_BREAK, TK_CASE, TK_CONST, TK_CONTINUE, TK_DEFAULT,
    TK_DO, TK_ELSE, TK_ELSEIF, TK_END, TK_FALSE, TK_FOR, TK_FUNCTION,
    TK_GLOBAL, TK_GOTO, TK_IF, TK_IN, TK_LAMBDA, TK_LOCAL, TK_NIL, TK_NOT, TK_OR, TK_REPEAT,
    TK_RETURN, TK_SWITCH, TK_THEN, TK_TRUE, TK_UNTIL, TK_WHEN, TK_WHILE,

    /* try-catch-finally */
    TK_TRY, TK_CATCH, TK_FINALLY, TK_DEFER,    // <-- 新增 TK_DEFER

    /* other terminal symbols */
    TK_IDIV, TK_CONCAT, TK_DOTS, TK_EQ, TK_GE, TK_LE, TK_NE,
    TK_SHL, TK_SHR,
    TK_DBCOLON, TK_EOS,
    TK_LET, TK_MEAN, TK_DARROW,
    TK_FLT, TK_INT, TK_NAME, TK_STRING,
    TK_QUESTION,
    TK_OPTDOT,
    TK_NULLCOAL,

    /* 复合赋值运算符 */
    TK_ADD_ASSIGN,
    TK_SUB_ASSIGN,
    TK_MUL_ASSIGN,
    TK_DIV_ASSIGN,
    TK_MOD_ASSIGN,
    TK_POW_ASSIGN,
    TK_CONCAT_ASSIGN,
    TK_BAND_ASSIGN,
    TK_BOR_ASSIGN,
    TK_SHL_ASSIGN,
    TK_SHR_ASSIGN,
    TK_IDIV_ASSIGN,
    TK_PIPE,
};

#define NUM_RESERVED    (TK_PIPE - FIRST_RESERVED + 1)

typedef union {
    lua_Number r;
    lua_Integer i;
    TString *ts;
} SemInfo;  /* semantics information */


typedef struct Token {
    int token;
    SemInfo seminfo;
} Token;


/* state of the lexer plus state of the parser when shared by all
   functions */
typedef struct LexState {
    int lasttoken;
    int curpos;
    int tokpos;
    int current;
    int linenumber;
    int lastline;
    int ternary_nest;  /* 三元运算符嵌套层级，0表示不在三元运算符中 */
    Token t;
    Token lookahead;
    struct FuncState *fs;
    struct lua_State *L;
    ZIO *z;
    Mbuffer *lastbuff;
    Mbuffer *buff;
    Table *h;
    struct Dyndata *dyd;
    TString *source;
    TString *envn;
} LexState;


LUAI_FUNC void luaX_init(lua_State *L);

LUAI_FUNC void luaX_setinput(lua_State *L, LexState *ls, ZIO *z,
                             TString *source, int firstchar);

LUAI_FUNC TString *luaX_newstring(LexState *ls, const char *str, size_t l);

LUAI_FUNC void luaX_next(LexState *ls);

LUAI_FUNC int luaX_lookahead(LexState *ls);

LUAI_FUNC l_noret luaX_syntaxerror(LexState *ls, const char *s);

LUAI_FUNC const char *luaX_token2str(LexState *ls, int token);

#endif