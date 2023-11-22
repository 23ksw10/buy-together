package com.together.buytogether.member.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class HashingUtil {

    public static String encrypt(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("알고리즘을 사용할 수 없습니다", e);
        }
        return byteToHex(md.digest());
    }

    private static String byteToHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte d : digest) {
            sb.append(String.format("02x", d));
        }
        return sb.toString();
    }
}
