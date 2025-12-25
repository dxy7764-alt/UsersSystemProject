package org.example.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.entity.Users;
import org.example.server.json.R;
import org.example.server.service.UsersService;
import org.example.server.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UsersController {

    @Resource
    private UsersService usersService;

    // ========== 修正：从Token解析uid和role（适配修改后的JwtUtils） ==========
    private Map<String, String> getCurrentUserInfo(HttpServletRequest request) {
        Map<String, String> userInfo = new HashMap<>();
        // 1. 从请求头获取前端传递的Token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            userInfo.put("error", "Token为空（未登录）");
            return userInfo;
        }
        // 2. 验证Token有效性（调用修改后的JwtUtils.verifyToken方法）
        if (!JwtUtils.verifyToken(token)) {
            userInfo.put("error", "Token无效或已过期");
            return userInfo;
        }
        // 3. 从Token中解析uid和role（调用新增的JwtUtils.getPayloadValue方法）
        Long uid = (Long) JwtUtils.getPayloadValue(token, "uid");
        String role = (String) JwtUtils.getPayloadValue(token, "role");
        // 4. 封装结果返回
        userInfo.put("uid", uid.toString());
        userInfo.put("role", role);
        return userInfo;
    }

    //用户注册的接口  获取资源还是提交资源？
    @PostMapping("/reg")
    public R register(@RequestBody Users users) {
        try {

            //新注册的用户默认性别是男，生日是2000-10-10
            if (users.getGender() == null || users.getGender().equals("")) {
                users.setGender("男");
            }
            if (users.getBirthday() == null) {
                users.setBirthday("2000-10-10");
            }

            boolean flag = usersService.register(users);
            if (flag) {
                return R.success("注册成功");
            }
            return R.fail("注册失败");
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }

    }

    //http://localhost:8080/usersys/users/login
    //用户登录的接口，用户登录按理说，应该是GET请求，但是GET请求，用户名和密码，通过URL明文传递参数的方式，不安全。
    @PostMapping("/login")
    public R login(HttpServletRequest request, @RequestBody Users users,
                   @RequestParam(value = "token", required = true) String token,
                   @RequestParam(value = "code", required = true) String code) {
        try {
            // 原有验证码校验逻辑（全部保留）
            if (request.getSession().getAttribute(token) == null) {
                return R.fail("验证码不正确！");
            }
            String sessionRandomCode = (String) request.getSession().getAttribute(token);
            if (!sessionRandomCode.equals(code)) {
                return R.fail("验证码不匹配！");
            }

            // 原有用户登录校验（全部保留）
            Users user = usersService.login(users.getUsername(), users.getPassword());
            if (user != null) {
                user.setPassword(""); // 清空密码，避免泄露
                // ========== 关键修改：生成Token时传入user.getRole（从数据库获取角色） ==========
                String jwtToken = JwtUtils.generateToken(user.getUid(), user.getUsername(), user.getRole());
                System.out.println("服务器颁发的Token： " + jwtToken);
                // 返回用户信息（包含role）和Token给前端
                return R.success("登录成功", user, jwtToken);
            }
            return R.fail("登录失败,请检查用户名或者密码是否正确！");
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }




    //查询所有用户的接口
    //GET/POST
    @GetMapping("/queryAll")
    public R queryAllUsers(HttpServletRequest request) {
        try {
            // 1. 获取当前用户的uid和role
            Map<String, String> userInfo = getCurrentUserInfo(request);
            if (userInfo.containsKey("error")) {
                return R.unauth(userInfo.get("error")); // 401未授权
            }
            String currentUid = userInfo.get("uid");
            String currentRole = userInfo.get("role");

            // 2. 按角色返回数据
            List<Users> usersList;
            if ("admin".equals(currentRole)) {
                // 管理员：返回所有用户（原有逻辑）
                usersList = usersService.queryAllUsers();
            } else {
                // 普通用户：只返回自己（调用新增的queryByUid方法）
                usersList = usersService.queryByUid(Long.valueOf(currentUid));
            }
            return R.success("查询所有用户资料成功！", usersList);
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }


    //根据用户编号查询用户的接口
    @GetMapping("/queryById/{uid}")
    public R queryUserById(@PathVariable Long uid) {
        try {
            Users user = usersService.getUserById(uid);
            return R.success("查询用户资料成功！", user);
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }

    //更新用户的接口
    @PutMapping("/update")
    public R updateUser(HttpServletRequest request, @RequestBody Users users) {
        try {
            // 1. 获取当前用户的uid和role
            Map<String, String> userInfo = getCurrentUserInfo(request);
            if (userInfo.containsKey("error")) {
                return R.unauth(userInfo.get("error"));
            }
            String currentUid = userInfo.get("uid");
            String currentRole = userInfo.get("role");

            // 2. 权限校验：普通用户只能修改自己的信息
            if (!"admin".equals(currentRole) && !users.getUid().equals(Long.valueOf(currentUid))) {
                return R.fail(403, "无权限修改他人信息！"); // 403权限不足
            }

            // 3. 原有更新逻辑（保留）
            boolean flag = usersService.updateUser(users);
            if (flag) {
                return R.success("更新用户资料成功！");
            }
            return R.fail("更新用户资料失败！");
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }

    //根据用户编号删除用户接口
    @DeleteMapping("/delete/{uid}")
    public R deleteUserById(HttpServletRequest request, @PathVariable Long uid) {
        try {
            // 1. 获取当前用户的role
            Map<String, String> userInfo = getCurrentUserInfo(request);
            if (userInfo.containsKey("error")) {
                return R.unauth(userInfo.get("error"));
            }
            String currentRole = userInfo.get("role");

            // 2. 权限校验：仅管理员可删除
            if (!"admin".equals(currentRole)) {
                return R.fail(403, "无权限执行删除操作！");
            }

            // 3. 原有删除逻辑（保留）
            boolean flag = usersService.deleteUserById(uid);
            if (flag) {
                return R.success("删除用户成功！");
            }
            return R.fail("删除用户失败！");
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }

    //根据用户名查询用户接口
    //@GetMapping("/queryByUsername/{username}")
    //public R queryUserByUsername(@PathVariable String username) //这种方式对中文不友好...注意....
    @GetMapping("/queryByUsername")
    public R queryUserByUsername(@RequestParam("username") String username) {
        try {
            List<Users> usersList = usersService.queryUserByUsername(username);
            return R.success("查询用户资料成功！", usersList);
        } catch (Exception ex) {
            return R.error("服务器发生异常...");
        }
    }

    // ========== 新增：按班级（banji）查询用户接口 ==========
    @GetMapping("/queryByBanji")
    public R queryUserByBanji(@RequestParam("banji") String banji) {
        try {
            // 调用Service层的按班级查询方法
            List<Users> usersList = usersService.queryUserByBanji(banji);
            // 兼容空结果场景，友好提示
            if (usersList.isEmpty()) {
                return R.success("未查询到该班级的用户数据！", usersList);
            }
            return R.success("按班级查询用户资料成功！", usersList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error("服务器发生异常...");
        }
    }

    @GetMapping("/auth")
    public R isLogin(@RequestParam("identify") String identify, @RequestParam("token") String token) {
        if (JwtUtils.verify(token, identify)) {
            return R.success("已登录！");
        } else {
            return R.unauth("未登录！");
        }
    }

    @GetMapping("/getUsersPager")
    public R queryUsersPager(@RequestParam("currentPageNum") Integer currentPageNum){
        try{
            Page<Users> pager = usersService.queryUsersPage(currentPageNum);
            return R.success("查询用户分页成功！",pager);
        }catch (Exception ex){
            ex.printStackTrace();
            return R.error("服务器发生异常....");
        }
    }

    @DeleteMapping("/deleteBatchUsers")
    public R deleteBatchUsers(HttpServletRequest request, @RequestBody List<Long> uids) {
        try {
            // 1. 权限校验：仅管理员可批量删除
            Map<String, String> userInfo = getCurrentUserInfo(request);
            if (userInfo.containsKey("error")) {
                return R.unauth(userInfo.get("error"));
            }
            if (!"admin".equals(userInfo.get("role"))) {
                return R.fail(403, "无权限执行批量删除操作！");
            }

            // 2. 原有批量删除逻辑（保留）
            boolean flag = usersService.deleteBatchUsers(uids);
            if (flag) {
                return R.success("批量删除用户成功！");
            }
            return R.fail("批量删除用户失败！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error("服务器发生异常...");
        }
    }

}