package com.exciting.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public final static DateTimeFormatter format_ymdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public final static DateTimeFormatter format_ymdhmssss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public final static SimpleDateFormat yyyyMMdd_format = new SimpleDateFormat("yyyy-MM-dd");

    public static String localDateTimeToString(LocalDateTime time,DateTimeFormatter formatter){
        if(time==null || formatter==null){
            return null;
        }
        return time.format(formatter);
    }

    public static String localDateToString(LocalDate time,DateTimeFormatter formatter){
        if(time==null || formatter==null){
            return null;
        }
        return time.format(formatter);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        if(date==null){
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static LocalDate dateToLocalDate(Date date) {
        return dateToLocalDateTime(date).toLocalDate();
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if(localDateTime==null){
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }
    public static Date localDateToDate(LocalDate localDate) {
        if(localDate==null){
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }


    public static Date stringToDate(String dateString) {
        if(StringUtils.isBlank(dateString)){
            return null;
        }
        return localDateTimeToDate(LocalDateTime.parse(dateString, format_ymdhms));
    }


    public static Date stringToDate(String dateString,DateTimeFormatter dateTimeFormatter) {
        if(StringUtils.isBlank(dateString)){
            return null;
        }
        return localDateTimeToDate(LocalDateTime.parse(dateString, dateTimeFormatter));
    }


    public static void main(String[] args) throws Exception {
        String s = DateUtils.localDateTimeToString(LocalDateTime.now(), format_ymdhms);
        System.out.println(s);
        //LocalDateTime.now().plusSeconds()
    }
}
