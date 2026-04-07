package com.lld.im.common.utils;

import org.apache.commons.codec.binary.Base64;
import java.nio.charset.StandardCharsets;

/**
 * @description: JDK17 修复版 Base64URL（apache commons-codec）
 * @author: lld
 * @version: 1.0
 */
public class Base64URL {

    // 编码：+→*  /→-  =→_
    public static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = new Base64().encode(input);
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        }
        return base64;
    }

    // 编码
    public static byte[] base64EncodeUrlNotReplace(byte[] input) {
        return base64EncodeUrl(input);
    }

    public static byte[] base64DecodeUrlNotReplace(byte[] input) {
        // 必须 clone，不修改原数组
        byte[] base64 = input.clone();
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '*':
                    base64[i] = '+';
                    break;
                case '-':
                    base64[i] = '/';
                    break;
                case '_':
                    base64[i] = '=';
                    break;
                default:
                    break;
            }
        }
        // 正确解码：byte[] 转字符串必须 new String
        return new Base64().decode(new String(base64, StandardCharsets.UTF_8));
    }

    public static byte[] base64DecodeUrl(byte[] input) {
        return base64DecodeUrlNotReplace(input);
    }
}