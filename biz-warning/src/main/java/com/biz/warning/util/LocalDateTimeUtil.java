package com.biz.warning.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeUtil {

    //默认使用系统当前时区
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    private static final String TIME_NOFUII_FORMAT = "yyyyMMddHHmmss";

    private static final String REGEX = "\\:|\\-|\\s";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String DATE_FORMAT_YYYY__MM__DD ="yyyy/MM/dd";
    /**
     * 将LocalDateTime转换成Date
     *
     * @param localDateTime
     * @return date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE).toInstant();
        return Date.from(instant);
    }

    /**
     * 根据传入的时间格式返回字符串
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY__MM__DD);
        return localDateTime.format(dateTimeFormatter);

    }
    /**
     *yyyy/mm/dd格式字符串转LocalDateTime
     *
     */
    public static LocalDateTime stringToLocalDateTime(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return  LocalDateTime.parse(str+" 00:00:00", formatter);
    }
    /**
     *yyyy/mm/dd格式字符串转LocalDate
     *
     */
    public static LocalDate stringToLocalDate(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return  LocalDate.parse(str, formatter);
    }
    /**
     * 根据传入的时间格式返回字符串
     *
     * @param localDate
     * @return
     */
    public static String localDateToString(LocalDate localDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY__MM__DD);
        return localDate.format(dateTimeFormatter);

    }
}
