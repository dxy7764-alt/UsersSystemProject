package org.example.server.utils;

import cn.hutool.crypto.digest.BCrypt;

public class MyUtils {

    //对明文密码进行加密
    public static String encodePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //对明文密码进行校验
    public static boolean checkPassword(String password, String hashedPassword) {

        return BCrypt.checkpw(password, hashedPassword);
    }

    public static void main(String[] args) {
        String password = "123456";
        for(int i=0;i<100;i++) {
            String hashedPassword = encodePassword(password);
            System.out.println(hashedPassword);

        }
    }
}
