package com.exciting.util;


import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

/**
 * 身份证工具类
 *
 * @author wujiaxing
 */
public class IdCardUtil {

    /**
     * 身份证号码最小长度
     */
    private final static int CHINA_ID_MIN_LENGTH = 15;

    /**
     * 身份证号码最大长度
     */
    private final static int CHINA_ID_MAX_LENGTH = 18;



    /**
     * 通过身份证计算年龄
     *
     * @param idCard 身份证号码
     * @return 返回0 身份证格式有错误
     */
    public static Integer getAgeByIdCard(String idCard) {
        Integer iAge = null;
        if (StringUtils.isNotBlank(idCard)) {
            Calendar cal = Calendar.getInstance();
            if (idCard.length() == CHINA_ID_MAX_LENGTH) {
                String year = idCard.substring(6, 10);
                int iCurrYear = cal.get(Calendar.YEAR);
                iAge = iCurrYear - Integer.valueOf(year);
            } else if (idCard.length() == CHINA_ID_MIN_LENGTH) {
                String year = idCard.substring(7, 9);
                year = "19" + year;
                int iCurrYear = cal.get(Calendar.YEAR);
                iAge = iCurrYear - Integer.valueOf(year);
            }
        }

        return iAge;
    }


    public static String getBirthday(String idCard) {
        String birthday = "";
        if (StringUtils.isNotBlank(idCard)) {
            if (idCard.length() == CHINA_ID_MAX_LENGTH) {
                String year = idCard.substring(6, 10);
                String month = idCard.substring(10,12);
                String day = idCard.substring(12,14);
                birthday = year+"-"+month+"-"+day;
            } else if (idCard.length() == CHINA_ID_MIN_LENGTH) {
                String year = idCard.substring(6, 8);
                year = "19" + year;
                String month = idCard.substring(8,10);
                String day = idCard.substring(10,12);
                birthday = year+"-"+month+"-"+day;
            }
        }
        return birthday;
    }

    public static void main(String[] args) {
        System.out.println(getSexByIdCard("342625199305060153"));
    }


    /**
     * 根据身份证号获取性别
     *
     * @param idCard 身份证号码
     * @return 性别
     */
    public static String getSexByIdCard(String idCard) {
        String sex;
        int resultNum = 0;
        if (StringUtils.isNotBlank(idCard) && idCard.length() == CHINA_ID_MIN_LENGTH) {
            resultNum = idCard.charAt(14);
        } else if (StringUtils.isNotBlank(idCard) && idCard.length() == CHINA_ID_MAX_LENGTH) {
            resultNum = idCard.charAt(16);
        }
        if (resultNum % 2 == 0) {
            sex = "woman";
        } else {
            sex = "man";
        }
        return sex;
    }


}
