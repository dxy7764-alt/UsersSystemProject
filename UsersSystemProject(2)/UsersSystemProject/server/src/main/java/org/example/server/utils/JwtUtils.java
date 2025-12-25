package org.example.server.utils;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 全局固定密钥（长度≥32字节，适配HS256算法）
    private static final String SECRET_KEY = "student_sys_2025_secure_key_with_32bytes!";
    // Token有效期：30天（单位：毫秒）
    private static final long EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    /**
     * 生成Token（兼容Hutool 5.8.38）
     * @param uid 用户ID
     * @param username 用户名
     * @param role 角色（admin/normal）
     * @return 签名后的Token字符串
     */
    public static String generateToken(Long uid, String username, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", uid);
        payload.put("username", username);
        payload.put("role", role);
        // 强制存储为Long类型，避免Hutool自动转换为NumberWithFormat
        payload.put("exp", Long.valueOf(System.currentTimeMillis() + EXPIRE_TIME));
        payload.put("iat", Long.valueOf(System.currentTimeMillis()));

        JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
        return JWT.create().addPayloads(payload).setSigner(signer).sign();
    }

    /**
     * 从Token中解析指定字段（适配5.8.38版本 + NumberWithFormat类型）
     * @param token Token字符串
     * @param field 字段名（如"uid"、"role"）
     * @return 字段值（无效Token返回null）
     */
    public static Object getPayloadValue(String token, String field) {
        try {
            if (!verifyToken(token)) {
                System.out.println("Token无效，无法解析字段：" + field);
                return null;
            }
            JWT jwt = JWTUtil.parseToken(token);
            // 统一处理NumberWithFormat类型，转换为原生类型
            Object value = jwt.getPayload().getClaim(field);
            return convertNumberWithFormat(value);
        } catch (Exception e) {
            System.err.println("解析Token字段失败（" + field + "）：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证Token有效性（适配5.8.38 + NumberWithFormat类型）
     * @param token Token字符串
     * @return 有效返回true，否则false
     */
    public static boolean verifyToken(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            jwt.setSigner(JWTSignerUtil.hs256(SECRET_KEY.getBytes()));

            // 1. 校验签名（5.8.38版本verify()方法兼容）
            if (!jwt.verify()) {
                System.out.println("Token签名验证失败");
                return false;
            }

            // 2. 校验过期时间（兼容NumberWithFormat类型）
            Object expObj = jwt.getPayload().getClaim("exp");
            if (expObj == null) {
                System.out.println("Token缺少过期时间（exp）字段");
                return false;
            }

            // 核心修复：处理NumberWithFormat类型，转换为Long（移除isInteger()调用）
            Long exp = convertToLong(expObj);
            if (exp == null) {
                System.out.println("exp字段类型错误：" + expObj.getClass().getName());
                return false;
            }

            if (exp < System.currentTimeMillis()) {
                System.out.println("Token已过期（当前时间：" + System.currentTimeMillis() + "，过期时间：" + exp + "）");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Token验证异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 辅助方法：将Object转换为Long（兼容NumberWithFormat/Integer/Long）
     * @param obj 待转换对象
     * @return 转换后的Long，失败返回null
     */
    private static Long convertToLong(Object obj) {
        try {
            if (obj instanceof Long) {
                return (Long) obj;
            } else if (obj instanceof Integer) {
                return ((Integer) obj).longValue();
            } else if (obj instanceof NumberWithFormat) {
                // 适配5.8.38：NumberWithFormat直接转Long（移除isInteger()）
                return ((NumberWithFormat) obj).longValue();
            } else if (obj instanceof Number) {
                return ((Number) obj).longValue();
            } else {
                // 尝试字符串转Long
                return Long.parseLong(obj.toString().trim());
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 辅助方法：统一转换NumberWithFormat为原生类型（移除isInteger()调用）
     * @param value 待转换值
     * @return 原生类型值（Long/String等）
     */
    private static Object convertNumberWithFormat(Object value) {
        if (value instanceof NumberWithFormat) {
            // 适配5.8.38：直接转Long，无需判断是否为整数
            NumberWithFormat nwf = (NumberWithFormat) value;
            return nwf.longValue();
        }
        return value;
    }

    /**
     * 保留原verify方法（兼容现有接口）
     * @param token Token字符串
     * @param username 用户名（仅为兼容，实际用全局密钥验证）
     * @return 验证结果
     */
    public static boolean verify(String token, String username) {
        try {
            return JWTUtil.verify(token, SECRET_KEY.getBytes());
        } catch (Exception e) {
            System.err.println("兼容验证失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 测试方法（验证5.8.38版本兼容性，无报错）
     */
    public static void main(String[] args) {
        // 生成测试Token
        String adminToken = generateToken(1L, "admin@test.com", "admin");
        String normalToken = generateToken(2L, "user@test.com", "normal");
        System.out.println("管理员Token：" + adminToken);
        System.out.println("普通用户Token：" + normalToken);

        // 解析字段测试
        Long adminUid = (Long) getPayloadValue(adminToken, "uid");
        String adminRole = (String) getPayloadValue(adminToken, "role");
        System.out.println("管理员解析结果：uid=" + adminUid + ", role=" + adminRole);

        // 有效性验证测试
        System.out.println("管理员Token是否有效：" + verifyToken(adminToken));
        System.out.println("普通用户Token是否有效：" + verifyToken(normalToken));
        System.out.println("兼容方法验证结果：" + verify(adminToken, "admin@test.com"));
    }
}