#ifndef TIME_H
#define TIME_H

#include <stdint.h>
#include <sys/time.h>

// 时间类型定义
typedef int64_t Timestamp;

/**
 * 获取当前时间戳（毫秒）
 * @return 当前时间戳，单位为毫秒
 */
Timestamp getCurrentTimestamp();

/**
 * 获取当前时间（秒）
 * @return 当前时间，单位为秒
 */
Timestamp getCurrentTime();

/**
 * 开始计时
 * @return 开始时间戳
 */
Timestamp startTime();

/**
 * 结束计时并返回经过的时间
 * @param start 开始时间戳
 * @return 经过的时间，单位为毫秒
 */
Timestamp endTime(Timestamp start);

/**
 * 创建倒计时
 * @param duration 倒计时时长，单位为毫秒
 * @return 倒计时结束时间戳
 */
Timestamp createCountdown(int64_t duration);

/**
 * 检查倒计时是否结束
 * @param endTime 倒计时结束时间戳
 * @return 1表示已结束，0表示未结束
 */
int isCountdownEnded(Timestamp endTime);

/**
 * 获取倒计时剩余时间
 * @param endTime 倒计时结束时间戳
 * @return 剩余时间，单位为毫秒，返回0表示已结束
 */
int64_t getCountdownRemaining(Timestamp endTime);

/**
 * 比较两个时间戳的差值
 * @param timestamp1 第一个时间戳
 * @param timestamp2 第二个时间戳
 * @return 两个时间戳的差值，单位为毫秒（timestamp1 - timestamp2）
 */
int64_t compareTimestamps(Timestamp timestamp1, Timestamp timestamp2);

#endif // TIME_H
