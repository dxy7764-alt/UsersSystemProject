package org.example.server.utils;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // ========== 新增：全局固定密钥（替换原username当密钥的逻辑，安全且统一） ==========
    // 建议：实际项目从application.yml读取，这里先定义为常量（至少8位，含数字/字母）
    private static final String SECRET_KEY = "student_sys_key_2025";
    // Token有效期：30天（和你原逻辑一致，单位：毫秒）
    private static final long EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    // ========== 修改1：生成Token（新增role参数，存到Payload） ==========
    public static String generateToken(Long uid, String username, String role) {
        // Payload：存储Token携带的核心信息（必须包含uid和role）
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", uid); // 用户ID（用于普通用户查自己）
        payload.put("username", username); // 用户名（辅助校验）
        payload.put("role", role); // 角色（核心！区分管理员/普通用户）
        payload.put("exp", System.currentTimeMillis() + EXPIRE_TIME); // 过期时间（Hutool自动识别）
        payload.put("iat", System.currentTimeMillis()); // 签发时间

        // 用全局密钥签名（替换原username.getBytes()，避免每个用户密钥不同）
        JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
        return JWT.create().addPayloads(payload).setSigner(signer).sign();
    }

    // ========== 新增1：从Token解析指定字段（如uid、role） ==========
    public static Object getPayloadValue(String token, String field) {
        try {
            // 先验证Token有效性，再解析字段
            if (!verifyToken(token)) {
                return null;
            }
            JWT jwt = JWTUtil.parseToken(token);
            return jwt.getPayload(field); // 返回指定字段值（需强转类型）
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ========== 新增2：验证Token有效性（签名+过期时间） ==========
    public static boolean verifyToken(String token) {
        try {
            // 1. 解析Token并设置全局密钥
            JWT jwt = JWTUtil.parseToken(token);
            jwt.setSigner(JWTSignerUtil.hs256(SECRET_KEY.getBytes()));

            // 2. 校验1：签名是否正确
            if (!jwt.verify()) {
                return false;
            }

            // 3. 校验2：手动判断Token是否过期（不依赖Hutool的JWTValidator，适配所有版本）
            Long exp = (Long) jwt.getPayload("exp");
            if (exp == null || exp < System.currentTimeMillis()) {
                return false; // Token已过期
            }

            return true; // 签名正确且未过期
        } catch (Exception e) {
            // 捕获“Token格式错误、签名错误、过期”等异常，均视为Token无效
            return false;
        }
    }

    // ========== 保留原verify方法（兼容你现有auth接口，不删除） ==========
    public static boolean verify(String token, String username) {
        // 注意：原逻辑用username当密钥，现在统一为全局密钥，username参数仅为兼容
        return JWTUtil.verify(token, SECRET_KEY.getBytes());
    }

    // ========== 测试方法（验证修改是否生效） ==========
    public static void main(String[] args) {
        // 生成管理员Token（role=admin）
        String adminToken = generateToken(1L, "admin@test.com", "admin");
        System.out.println("管理员Token：" + adminToken);

        // 生成普通用户Token（role=normal）
        String normalToken = generateToken(2L, "user@test.com", "normal");
        System.out.println("普通用户Token：" + normalToken);

        // 解析Token字段
        Long adminUid = (Long) getPayloadValue(adminToken, "uid");
        String adminRole = (String) getPayloadValue(adminToken, "role");
        System.out.println("管理员解析结果：uid=" + adminUid + ", role=" + adminRole); // 预期：1, admin

        // 验证Token有效性
        System.out.println("管理员Token是否有效：" + verifyToken(adminToken)); // 预期：true
        System.out.println("普通用户Token是否有效：" + verifyToken(normalToken)); // 预期：true
    }
}