package com.together.buytogether.member.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {

    public String encrypt(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
        } catch (NoSuchAlgorithmException e) {

        }
        return byteToHex(md.digest());
    }

    private String byteToHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte d : digest) {
            sb.append(String.format("02x", d));
        }
        return sb.toString();
    }
}
