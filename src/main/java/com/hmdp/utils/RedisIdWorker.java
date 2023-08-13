package com.hmdp.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1672531200L;

    //序列号位数
    private static final int COUNT_BITS = 32;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public long nextId(String keyPrefix){
        // 1. 生成当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;

        // 2. 生成序列号
        // 利用每天的日期生成key, 避免同一个key超过32次方，同时也有统计的效果
        //  2.1 获取当前的日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        //  2.2 自增长
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);


        //  3. 拼接并返回  位运算
        return timeStamp << COUNT_BITS | count;
    }

//    public static void main(String[] args) {
//        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
//        long second = time.toEpochSecond(ZoneOffset.UTC);
//        System.out.println(second);
//    }

}
