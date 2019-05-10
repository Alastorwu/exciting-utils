package com.exciting.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
　* @Description:校验工具类
　* @author Robb
　* @date 2018/4/23 10:18
　*/
@SuppressWarnings("ALL")
public class ValidateUtils {
    public static boolean isPassword(String pwd) {
        return !isInteger(pwd) && (!isLettersOnly(pwd) && isAlphanumeric(pwd));
    }

    public static boolean isAlphanumeric(String str) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        return pattern.matcher(str).matches();
    }

    public static boolean isLettersOnly(String str) {
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        return pattern.matcher(str).matches();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isDoubleAnd2decimals(String str) {
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))([.](\\d){1,2})?$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumeric(String str) {
        return !StringUtils.isEmpty(str) && str.matches("\\d*");
    }

    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[Α-￥]+$");
        return pattern.matcher(str).matches();
    }

    public static boolean isPrime(int x) {
        if (x > 7 || x != 2 && x != 3 && x != 5 && x != 7) {
            int c = 7;
            if (x % 2 == 0) {
                return false;
            } else if (x % 3 == 0) {
                return false;
            } else if (x % 5 == 0) {
                return false;
            } else {
                for (int end = (int) Math.sqrt((double) x); c <= end; c += 6) {
                    if (x % c == 0) {
                        return false;
                    }

                    c += 4;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 2;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 4;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 2;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 4;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 6;
                    if (x % c == 0) {
                        return false;
                    }

                    c += 2;
                    if (x % c == 0) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean isMobile(String mobile) {
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
        return pattern.matcher(mobile).matches();
    }

    public static boolean isPhone(String phone) {
        Pattern pattern = Pattern.compile("^0[0-9]{2,3}[-|－][0-9]{7,8}([-|－][0-9]{1,4})?$");
        return pattern.matcher(phone).matches();
    }

    public static boolean isPostCode(String post) {
        Pattern pattern = Pattern.compile("^[0-9]{6}$");
        return pattern.matcher(post).matches();
    }


    public static boolean isTime(String timeStr) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        Date date = null;

        try {
            date = df.parse(timeStr);
        } catch (ParseException arg3) {
            return false;
        }

        return date != null;
    }

    public static boolean isDateTime(String dateTime) {
        int first = dateTime.indexOf(":");
        int last = dateTime.lastIndexOf(":");
        if (first == -1) {
            return false;
        } else {
            SimpleDateFormat df = null;
            if (first == last) {
                df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }

            Date date;

            try {
                date = df.parse(dateTime);
            } catch (ParseException arg5) {
                return false;
            }

            return date == null;
        }
    }

    public static boolean isCapitalBar(String frpCode) {
        Pattern pattern = Pattern.compile("^[A-Z]+[-－][A-Z[22]]+(\\$[A-Z]+[-－][A-Z[22]]+)*");
        return pattern.matcher(frpCode).matches();
    }

    public static boolean isIP(String ip) {
        Pattern pattern = Pattern.compile(
                "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$");
        return pattern.matcher(ip).matches();
    }

    public static boolean isMac(String mac) {
        Pattern pattern = Pattern.compile("^([0-9a-fA-F]{2})(([\\s:-][0-9a-fA-F]{2}){5})$");
        return pattern.matcher(mac).matches();
    }

    public static boolean isQQ(String qq) {
        Pattern pattern = Pattern.compile("^[1-9][0-9]{4,14}$");
        return pattern.matcher(qq).matches();
    }

    public static boolean isBankCard(String bankCard) {
        if (StringUtils.isNotEmpty(bankCard)) {
            String nonCheckCodeCardId = bankCard.substring(0, bankCard.length() - 1);
            if (nonCheckCodeCardId.matches("\\d+")) {
                char[] chs = nonCheckCodeCardId.toCharArray();
                int luhmSum = 0;
                int b = chs.length - 1;

                for (int j = 0; b >= 0; ++j) {
                    int k = chs[b] - 48;
                    if (j % 2 == 0) {
                        k *= 2;
                        k = k / 10 + k % 10;
                    }

                    luhmSum += k;
                    --b;
                }

                char arg6 = luhmSum % 10 == 0 ? 48 : (char) (10 - luhmSum % 10 + 48);
                return bankCard.charAt(bankCard.length() - 1) == arg6;
            }
        }

        return false;
    }

    private static List<String> generateBankCard(int count) {
        long l = 100000000000000000L;
        ArrayList list = new ArrayList();

        for (int a = 1; a <= count; ++a) {
            String s = String.valueOf(l + (long) a);
            char[] chs = s.toCharArray();
            int luhmSum = 0;
            int b = chs.length - 1;

            for (int bankcard = 0; b >= 0; ++bankcard) {
                int k = chs[b] - 48;
                if (bankcard % 2 == 0) {
                    k *= 2;
                    k = k / 10 + k % 10;
                }

                luhmSum += k;
                --b;
            }

            char arg10 = luhmSum % 10 == 0 ? 48 : (char) (10 - luhmSum % 10 + 48);
            String arg11 = s + arg10;
            if (isBankCard(arg11)) {
                list.add(s + arg10);
            }
        }

        return list;
    }

    public static void checkStrMinLength(String str, Integer minLength, String message) {
        if (str.trim().length() < minLength.intValue()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean checkStrMinLengthByBytes(String str, Integer minLength) {
        int length = str.getBytes().length;
        return length >= minLength.intValue();
    }

    public static boolean checkStrMaxLengthByBytes(String str, Integer maxLength) {
        int length = str.getBytes().length;
        return length <= maxLength.intValue();
    }

    public static void checkStrMaxLength(String str, Integer maxLength, String message) {
        if (str.trim().length() > maxLength.intValue()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotEmpty(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        } else if (obj instanceof String && obj.toString().trim().length() == 0) {
            throw new IllegalArgumentException(message);
        } else if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            throw new IllegalArgumentException(message);
        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            throw new IllegalArgumentException(message);
        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || (obj instanceof String && obj.toString().trim().length() == 0 || (obj.getClass()
                .isArray() && Array.getLength(obj) == 0 || (obj instanceof Collection && ((Collection) obj).isEmpty()
                || obj instanceof Map && ((Map) obj).isEmpty()
        )));
    }

    public static boolean isIdCard(String idCard) {
        Pattern pattern = Pattern.compile("^(^\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
        return pattern.matcher(idCard).matches();
    }

    public static final boolean isOrgCode(String orgCode) {
        String[] codeNo = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String[] staVal = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
                "32", "33", "34", "35"};
        Pattern pat = Pattern.compile("^[0-9A-Z]{8}-[0-9X]$");
        Matcher matcher = pat.matcher(orgCode);
        if (!matcher.matches()) {
            return false;
        } else {
            HashMap map = new HashMap();

            for (int wi = 0; wi < codeNo.length; ++wi) {
                map.put(codeNo[wi], staVal[wi]);
            }

            int[] arg11 = new int[]{3, 7, 9, 10, 5, 8, 4, 2};
            String[] all = orgCode.split("-");
            char[] values = all[0].toCharArray();
            int parity = 0;

            for (int cheak = 0; cheak < values.length; ++cheak) {
                String val = Character.toString(values[cheak]);
                parity += arg11[cheak] * Integer.parseInt(map.get(val).toString());
            }

            String arg12 = 11 - parity % 11 == 10 ? "X" : Integer.toString(11 - parity % 11);
            return arg12.equals(all[1]);
        }
    }

    public static boolean isPassport(String passport) {
        Pattern pattern = Pattern.compile("^1[45][0-9]{7}|G[0-9]{8}|E[0-9]{8}|P[0-9]{7}|S[0-9]{7,8}|D[0-9]+$");
        return pattern.matcher(passport).matches();
    }

    public static boolean isHMTPass(String hmtPass) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{5,21}$");
        return pattern.matcher(hmtPass).matches();
    }

    public static boolean isTBPass(String hmtPass) {
        Pattern pattern = Pattern.compile("^[0-9]{10}\\(+B\\)+$");
        return pattern.matcher(hmtPass).matches();
    }
}
