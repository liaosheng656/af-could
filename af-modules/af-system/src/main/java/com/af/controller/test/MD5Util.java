package com.af.controller.test;

import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * MD5 工具类
 * update on 2018/7/10 15:20
 */
@SuppressWarnings("CheckStyle")
public class MD5Util {

    /**
     * 获取签名规则sign
     *
     * @param str 需要计算MD5的字符串
     * @return MD5值(16进制字符串)
     */
    public static String getSign(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //md5加密
            byte[] result = md5.digest(str.getBytes("utf-8"));
            //转成16进制字符串，通过算法变成64位字符串
            return mergeStr(bytesToHexFun3(result)).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 32位MD5加密
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5 = new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        String test = "Hello";
        System.out.println(getSign(test));
        System.out.println(getMD5(test));
    }


    /**
     * 获取签名规则sign
     *
     * @param rawText 需要计算MD5的字符串
     * @return MD5值(16进制字符串)
     */
    public static String getMD5Hash(String rawText) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (m == null) {
            return null;
        }
        m.reset();
        try {
            m.update(rawText.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText.toUpperCase();
    }


    /**
     * Convert byte[] to hex string.来转换成16进制字符串。
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String bytesToHexFun3(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", Integer.valueOf(b & 0xff)));
        }
        return buf.toString();
    }


    /**
     * 用位运算倒序字符串
     *
     * @param str 字符串
     * @return 倒序后的字符数组
     */
    public static char[] reverseStr(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char[] chArray = str.toCharArray();
        int len = str.length();
        for (int i = 0; i < len / 2; i++) {
            chArray[i] ^= chArray[len - 1 - i];
            chArray[len - 1 - i] ^= chArray[i];
            chArray[i] ^= chArray[len - 1 - i];
        }
        return chArray;
    }

    /**
     * 将字符串与其倒序位运算后合并截取
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String mergeStr(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] revChar = reverseStr(str);
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
            sb.append(revChar[i] ^ str.charAt(i));
        }
        int len = sb.length() - 1;
        return sb.substring(len - 64, len);
    }

    /**
     * 进行SHA1运算
     *
     * @param data 字符串
     * @return SHA-1加密后得到的字节数组
     * @throws IOException 无法获取SHA-1加密方式
     */
    public static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.getMessage());
        }
        return bytes;
    }

    /**
     * 进行SHA1运算
     *
     * @param data 字符串
     * @param salt 盐值
     * @return SHA-1加密后得到的字节数组
     * @throws IOException 无法获取SHA-1加密方式
     */
    public static String getSHA1Digest(String data, String salt) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt.getBytes());
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.getMessage());
        }
        return byte2hex(bytes);
    }



    /**
     * 二进制转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }


    /**
     * 使用 Map按key进行排序  升序
     *
     * @param map 目标Map
     * @return 升序排序后的Map
     */
    public static Map<String, String> ascMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> ascMap = new TreeMap<String, String>(new MapKeyComparator());

        ascMap.putAll(map);

        return ascMap;
    }

    /**
     * 用于Map排序方法使用
     * created on 2018/8/15 11:36
     */

    static class MapKeyComparator implements Comparator<String>,Serializable {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }


    /**
     * 将字节数组转换成16进制字符串
     *
     * @param byteArray 要转码的字节数组
     * @return 返回转码后的16进制字符串
     */
    public static String byteArrayToHexStr(byte byteArray[]) {
        StringBuffer buffer = new StringBuffer(byteArray.length * 2);
        int i;
        for (i = 0; i < byteArray.length; i++) {
            if (((int) byteArray[i] & 0xff) < 0x10)// 小于十前面补零
                buffer.append("0");
            buffer.append(Long.toString((int) byteArray[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    /**
     * 信息摘要算法
     *
     * @param algorithm
     *            算法类型
     * @param data
     *            要加密的字符串
     * @param charSet
     *            编码的字符集
     * @return 返回加密后的摘要信息
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encryptEncode(String algorithm, String data, String charSet) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        return byteArrayToHexStr(md.digest(data.getBytes(charSet)));
    }

    public static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }

    /**
     * @funcion 对文件全文生成MD5摘要  
     * @param file 要加密的文件 
     * @return MD5摘要码 
     */
    public static String getMD5File(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] b = md.digest();
            return byte2hex(b).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(fis!=null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 内部API接口请求报文计算MD5签名
     * @param body
     * @param privateKey
     * @return
     */
    public static String computeSign(com.alibaba.fastjson.JSONObject body, String privateKey) {


        StringBuilder sb = new StringBuilder();
        body.remove("sign");

        body.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sb.append(x.getKey()).append("=").append(sortStr(String.valueOf(x.getValue()))));
        sb.append(privateKey);
        return MD5Util.getMD5(sb.toString());
    }

    private static String sortStr(String text) {

        char[] test = new char[text.length()];
        StringBuilder sb = new StringBuilder();
        while (true) {
            String a = text;//直接读取这行当中的字符串。
            for (int i = 0; i < text.length(); i++) {
                test[i] = a.charAt(i);//字符串处理每次读取一位。
            }
            Arrays.sort(test);
            for (int i = 0; i < test.length; i++) {
                sb.append(test[i]);
            }
            String trim = sb.toString().trim();
            return trim;
        }
    }
}
