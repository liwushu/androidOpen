/*
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 * 
 * FileName: StringUtils.java
 * 
 * Description: 字符串操作类文件
 * 
 * History: 1.0 devilxie 2012-09-05 Create
 */

package com.tcl.update.framework.util;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;

/**
 * 字符串辅助处理类，主要提供字节数组转16进制字符，按GBK比较字符串以及特殊字符的转义
 * 
 * @author devilxie
 * @version 1.0
 */
@SuppressLint("DefaultLocale")
public class StringUtils {
    /**
     * 
     * 字符解析器
     */
    public interface CharSpeller {
        String spell(char c);
    }

    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static CharSpeller speller = null;

    public static void registerSpeller(CharSpeller cs) {
        speller = cs;
    }

    /**
     * 将指定字节转换成16进制字符
     * 
     * @param b 待转换字节
     * @return 返回转换后的字符串
     */
    public static String byteToHexDigits(byte b) {
        int n = b;
        if (n < 0) n += 256;

        int d1 = n / 16;
        int d2 = n % 16;

        return "" + HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 将指定字节数组转换成16进制字符串
     * 
     * @param bytes 待转换的字节数组
     * @return 返回转换后的字符串
     */
    public static String bytesToHexes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHexDigits(bytes[i]));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符转换为整数
     * 
     * @param hex 小写十六进制字符
     * @return 返回整数
     */
    public static int hexToInteger(char hex) {
        if (hex >= HEX_DIGITS[16])
            return hex - HEX_DIGITS[16] + 10;
        else if (hex >= HEX_DIGITS[10])
            return hex - HEX_DIGITS[10] + 10;
        else
            return hex - HEX_DIGITS[0];
    }

    /**
     * 十六进制字符串转换为字节数组
     * 
     * @param hexes 十六进制字符串
     * @return 返回字节数组
     */
    public static byte[] hexesToBytes(String hexes) {
        if (hexes == null || hexes.length() == 0) return null;

        int slen = hexes.length();
        int len = (slen + 1) / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            char c = hexes.charAt(2 * i);
            int val = hexToInteger(c);
            val *= 16;
            if (2 * i + 1 < slen) {
                c = hexes.charAt(2 * i + 1);
                val += hexToInteger(c);
            }

            bytes[i] = (byte) (val & 0xff);

        }
        return bytes;
    }

    /**
     * 比较两个字符串大小，考虑汉字拼音顺序, 忽略大小写
     * 
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 返回比较结果。0： s1 = s2， >0： s1 > s2, <0: s1 < s2
     */
    @SuppressLint("DefaultLocale")
    public static int compareToIgnoreCase(String s1, String s2) {
        // 两者为空，相同
        if (s1 == null && s2 == null) {
            return 0;
        }
        // 某项为空，则以它为小
        if (s1 == null) {
            return -1;
        }

        if (s2 == null) {
            return 1;
        }

        if (s1.equals(s2)) {
            return 0;
        }

        String s3 = s1.toLowerCase();
        String s4 = s2.toLowerCase();

        return compareToUnicode(s3, s4);
    }

    /**
     * 比较两个字符串大小，考虑汉字拼音顺序
     * 
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 返回比较结果。0： s1 = s2， >0： s1 > s2, <0: s1 < s2
     */
    public static int compareTo(String s1, String s2) {

        // 两者为空，相同
        if (s1 == null && s2 == null) {
            return 0;
        }
        // 某项为空，则以它为小
        if (s1 == null) {
            return -1;
        }

        if (s2 == null) {
            return 1;
        }

        if (s1.equals(s2)) {
            return 0;
        }

        if (s1.length() == 0) {
            return -1;
        }

        if (s2.length() == 0) {
            return 1;
        }

        return compareToUnicode(s1, s2);
    }

    public static boolean isLetter(int ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    public static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static final int UPPER_LOWER_SPAN = 'A' - 'a';
    static final int LOWER_UPPER_SPAN = -UPPER_LOWER_SPAN;

    private static int compareToGBK(String s1, String s2) {
        int ret = 0;
        try {
            byte[] bytes1 = s1.getBytes("gbk");
            byte[] bytes2 = s2.getBytes("gbk");

            int len = Math.min(bytes1.length, bytes2.length);
            for (int i = 0; i < len; i++) {

                if (bytes1[i] > 0 && bytes2[i] > 0) {
                    ret = Character.toLowerCase(bytes1[i]) - Character.toLowerCase(bytes2[i]);
                    if (ret == 0) ret = bytes1[i] - bytes2[i];
                } else {
                    int b1 = (bytes1[i] + 256) % 256;
                    int b2 = (bytes2[i] + 256) % 256;
                    ret = b1 - b2;
                }

                if (ret != 0) {
                    break;
                }

            }
            if (ret == 0) {
                ret = bytes1.length - bytes2.length;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static int compareToUnicode(String s1, String s2) {

        if (speller == null) {
            return compareToGBK(s1, s2);
        }

        int ret = 0;
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            ret = compareTo(c1, c2);
            if (ret != 0) break;
        }

        if (ret == 0) {
            ret = s1.length() - s2.length();
        }

        return ret;
    }

    private static int compareTo(char c1, char c2) {
        int ret = 0;
        // 字母比较，直接比较ASCII
        if (isLetter(c1) && isLetter(c2)) {
            ret = Character.toLowerCase(c1) - Character.toLowerCase(c2);
            if (ret == 0) ret = c1 - c2;

            return ret;
        }

        String s1 = null;
        String s2 = null;

        if (isLetter(c1)) {
            s2 = speller.spell(c2);
            char cc2 = s2.charAt(0);
            if (isLetter(cc2)) {
                ret = Character.toLowerCase(c1) - Character.toLowerCase(cc2);
                if (ret == 0) {
                    ret = 1;
                }

                return ret;
            } else
                return -1;
        }

        else if (isLetter(c2)) {
            s1 = speller.spell(c1);
            char cc1 = s1.charAt(0);
            if (isLetter(cc1)) {
                ret = Character.toLowerCase(cc1) - Character.toLowerCase(c2);
                if (ret == 0) {
                    ret = -1;
                }

                return ret;
            } else {
                return 1;
            }
        } else {
            s1 = speller.spell(c1);
            s2 = speller.spell(c2);
        }

        int len = Math.min(s1.length(), s2.length());

        for (int i = 0; i < len; i++) {
            char cc1 = s1.charAt(i);
            char cc2 = s2.charAt(i);

            if (isLetter(cc1) && isLetter(cc2)) {
                ret = Character.toLowerCase(cc1) - Character.toLowerCase(cc2);
                if (ret == 0) {
                    ret = cc1 - cc2;
                }
            }

            else if (isLetter(cc1)) {
                ret = -1;
            }

            else if (isLetter(cc2)) {
                ret = 1;
            }

            else {
                ret = cc1 - cc2;
            }

            if (ret != 0) {
                break;
            }
        }

        if (ret == 0) {
            ret = s1.length() - s2.length();
        }

        return ret;
    }

    @SuppressWarnings("unused")
    private static int compareToBigInteger(String s1, String s2) {
        int ret = 0;
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();

        int index1 = 0, index2 = 0;
        while (index1 < c1.length && c1[index1] == '0')
            index1++;
        while (index2 < c2.length && c2[index2] == '0')
            index2++;

        if (c1.length - index1 != c2.length - index2) {
            ret = (c1.length - index1) - (c2.length - index2);
        } else {
            ret = c1[index1] - c2[index2];
        }

        return ret;
    }

    /**
     * 将作为文件名的字符串的特殊字符"\*?:$/'",`^<>+"替换成"_"，以便文件顺利创建成功
     * 
     * @param path 原待创建的文件名
     * @return 返回处理后的文件名
     */
    public static String filterForFile(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        String need = path.replaceAll("\\\\|\\*|\\?|\\:|\\$|\\/|'|\"|,|`|\\^|<|>|\\+", "_");
        return need;
    }
}
