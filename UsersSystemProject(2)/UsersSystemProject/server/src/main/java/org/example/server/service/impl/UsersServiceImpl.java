package org.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.server.entity.Users;
import org.example.server.mapper.UsersMapper;
import org.example.server.service.UsersService;
import org.example.server.utils.MyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // 新增：导入Map相关包


@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Resource
    private UsersMapper usersMapper;
    @Value("${mybatis-plus.pager_size}")
    private Integer pageSize;

    // ========== 1. 实现用户注册方法（适配3.0.5） ==========
    @Override
    public boolean register(Users users) {
        if (users == null || !StringUtils.hasText(users.getUsername())) {
            return false;
        }
        if (StringUtils.hasText(users.getPassword())) {
            users.setPassword(MyUtils.encodePassword(users.getPassword()));
        }
        return usersMapper.insert(users) > 0;
    }

    // ========== 2. 实现用户登录方法（适配3.0.5） ==========
    @Override
    public Users login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<Users>();
        queryWrapper.eq("username", username);
        Users user = usersMapper.selectOne(queryWrapper);
        if (user != null && MyUtils.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // ========== 3. 实现根据用户编号查询单个用户（适配3.0.5） ==========
    @Override
    public Users getUserById(Long uid) {
        if (uid == null || uid <= 0) {
            return null;
        }
        return usersMapper.selectById(uid);
    }

    // ========== 4. 实现查询所有用户（适配3.0.5） ==========
    @Override
    public List<Users> queryAllUsers() {
        List<Users> list = usersMapper.selectList(new QueryWrapper<Users>());
        return list == null ? Collections.emptyList() : list;
    }

    // ========== 5. 实现用户资料分页查询（适配3.0.5） ==========
    @Override
    public Page<Users> queryUsersPage(Integer currentPageNum) {
        if (currentPageNum == null || currentPageNum < 1) {
            currentPageNum = 1;
        }
        Page<Users> page = new Page<Users>(currentPageNum, pageSize);
        IPage<Users> iPage = usersMapper.selectPage(page, new QueryWrapper<Users>());
        return (Page<Users>) iPage;
    }

    // ========== 6. 实现更新用户（适配3.0.5） ==========
    @Override
    public boolean updateUser(Users users) {
        if (users == null || users.getUid() == null) {
            return false;
        }
        return usersMapper.updateById(users) > 0;
    }

    // ========== 7. 实现根据用户名查询用户（适配3.0.5） ==========
    @Override
    public List<Users> queryUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptyList();
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<Users>();
        queryWrapper.like("username", username);
        List<Users> list = usersMapper.selectList(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    // ========== 8. 实现根据用户编号删除用户（适配3.0.5） ==========
    @Override
    public boolean deleteUserById(Long uid) {
        if (uid == null || uid <= 0) {
            return false;
        }
        return usersMapper.deleteById(uid) > 0;
    }

    // ========== 9. 实现批量删除用户（适配3.0.5） ==========
    @Override
    public boolean deleteBatchUsers(List<Long> uids) {
        if (uids == null || uids.isEmpty()) {
            return false;
        }
        return usersMapper.deleteBatchIds(uids) > 0;
    }

    // ========== 按班级查询用户（适配3.0.5） ==========
    @Override
    public List<Users> queryUserByBanji(String banji) {
        if (!StringUtils.hasText(banji)) {
            return Collections.emptyList();
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<Users>();
        queryWrapper.eq("banji", banji);
        return usersMapper.selectList(queryWrapper);
    }

    // ========== 按用户ID查询自己（适配3.0.5） ==========
    @Override
    public List<Users> queryByUid(Long uid) {
        if (uid == null) {
            return Collections.emptyList();
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<Users>();
        queryWrapper.eq("uid", uid);
        return usersMapper.selectList(queryWrapper);
    }

}