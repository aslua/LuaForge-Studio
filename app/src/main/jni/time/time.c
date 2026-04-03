#include "time.h"
#include <stdint.h>
#include <sys/time.h>

/**
 * 获取当前时间戳（毫秒）
 * @return 当前时间戳，单位为毫秒
 */
Timestamp getCurrentTimestamp() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (Timestamp) tv.tv_sec * 1000 + (Timestamp) tv.tv_usec / 1000;
}

/**
 * 获取当前时间（秒）
 * @return 当前时间，单位为秒
 */
Timestamp getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (Timestamp) tv.tv_sec;
}

/**
 * 开始计时
 * @return 开始时间戳
 */
Timestamp startTime() {
    return getCurrentTimestamp();
}

/**
 * 结束计时并返回经过的时间
 * @param start 开始时间戳
 * @return 经过的时间，单位为毫秒
 */
Timestamp endTime(Timestamp start) {
    return getCurrentTimestamp() - start;
}

/**
 * 创建倒计时
 * @param duration 倒计时时长，单位为毫秒
 * @return 倒计时结束时间戳
 */
Timestamp createCountdown(int64_t duration) {
    return getCurrentTimestamp() + duration;
}

/**
 * 检查倒计时是否结束
 * @param endTime 倒计时结束时间戳
 * @return 1表示已结束，0表示未结束
 */
int isCountdownEnded(Timestamp endTime) {
    return getCurrentTimestamp() >= endTime;
}

/**
 * 获取倒计时剩余时间
 * @param endTime 倒计时结束时间戳
 * @return 剩余时间，单位为毫秒，返回0表示已结束
 */
int64_t getCountdownRemaining(Timestamp endTime) {
    Timestamp now = getCurrentTimestamp();
    return now >= endTime ? 0 : endTime - now;
}

/**
 * 比较两个时间戳的差值
 * @param timestamp1 第一个时间戳
 * @param timestamp2 第二个时间戳
 * @return 两个时间戳的差值，单位为毫秒（timestamp1 - timestamp2）
 */
int64_t compareTimestamps(Timestamp timestamp1, Timestamp timestamp2) {
    return timestamp1 - timestamp2;
}
