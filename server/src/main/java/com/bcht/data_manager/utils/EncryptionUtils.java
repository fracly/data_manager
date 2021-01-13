package com.bcht.data_manager.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class EncryptionUtils {

    public static final String getMD5(String plain) {
        return DigestUtils.md5Hex(plain == null ? "" : plain);
    }
}
