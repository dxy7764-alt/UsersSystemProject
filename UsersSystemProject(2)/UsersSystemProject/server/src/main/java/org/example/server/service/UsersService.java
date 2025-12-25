package org.example.server.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.server.entity.Users;
import java.util.List;
import java.util.Map;

public interface UsersService extends IService<Users> {
    //1.用户注册
    boolean register(Users users);
    //2.用户登录
    Users login(String username, String password);
    //3.根据用户编号查询单个用户
    Users getUserById(Long uid);
    //4.查询所有的用户
    List<Users> queryAllUsers();
    //用户资料的分页查询
    Page<Users> queryUsersPage(Integer currentPageNum);
    //5.更新用户
    boolean updateUser(Users users);
    //6.根据用户名查询用户资料
    List<Users> queryUserByUsername(String username);
    //7.根据用户编号删除用户
    boolean deleteUserById(Long uid);
    boolean deleteBatchUsers(List<Long> uids);
    // ========== 按班级查询用户 ==========
    List<Users> queryUserByBanji(String banji);
    // ========== 按用户ID查询自己（普通用户专用） ==========
    List<Users> queryByUid(Long uid);

}