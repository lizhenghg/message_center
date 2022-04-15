package com.cracker.api.mc.common.codec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 随机数/id/加减密算法/编码解码 实现类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-12-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class Codec {


    public static String getRandomByUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRandom() {
        return randomString(36);
    }

    public static String randomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String encodeBase64(String value) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(value.getBytes(StandardCharsets.UTF_8.name())));
    }

    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }


    public static byte[] decodeBase64(String value) throws UnsupportedEncodingException {
        return Base64.decodeBase64(value.getBytes(StandardCharsets.UTF_8.name()));
    }

    public static String decodeBase64(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes));
    }


    public static String getHexMd5(String value) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8.name()));
        byte[] digest = messageDigest.digest();
        return byteToHexString(digest);
    }

    public static String getHexSha1(String value) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        messageDigest.reset();
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8.name()));
        byte[] digest = messageDigest.digest();
        return byteToHexString(digest);
    }


    public static String byteToHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hexStringToByte(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString.toCharArray());
    }
}