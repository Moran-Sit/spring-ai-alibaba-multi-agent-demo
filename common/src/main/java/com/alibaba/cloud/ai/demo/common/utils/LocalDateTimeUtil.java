package com.alibaba.cloud.ai.demo.common.utils;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    public static final DateTimeFormatter PATTERN_DEFAULT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter PATTERN_YYYYMMDDMMHHSS = DateTimeFormatter.ofPattern("yyyyMMddmmHHss");
    public static final DateTimeFormatter PATTERN_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDateTimeUtil() {
    }

    public static LocalDateTime parse(String localDateTime) {
        return LocalDateTime.parse(localDateTime, PATTERN_DEFAULT);
    }

    public static LocalDateTime parse(Long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    public static String format(LocalDateTime localDateTime) {
        return format(localDateTime, PATTERN_DEFAULT);
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        if (null == localDateTime) {
            return StringUtils.EMPTY;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime localDateTime, DateTimeFormatter pattern) {
        if (null == localDateTime) {
            return StringUtils.EMPTY;
        }
        return localDateTime.format(pattern);
    }

    public static int getIntDay(LocalDateTime localDateTime) {
        return localDateTime.getYear() * 10000 + localDateTime.getMonthValue() * 100 + localDateTime.getDayOfMonth();
    }

    public static int getToday() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() * 10000 + now.getMonthValue() * 100 + now.getDayOfMonth();
    }
}
