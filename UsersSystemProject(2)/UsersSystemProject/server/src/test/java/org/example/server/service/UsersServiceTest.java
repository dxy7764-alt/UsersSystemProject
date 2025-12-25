package org.example.server.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.entity.Users;
import org.example.server.mapper.UsersMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.List;
import java.util.ArrayList;


@SpringBootTest
@RunWith(SpringRunner.class)
public class UsersServiceTest {

    @Resource
    private UsersService usersService;

    @Resource
    private UsersMapper usersMapper;

    @Test
    public void testRegister() {
        Users users = new Users();
        users.setUsername("刘备");
        users.setPassword("123456");
        users.setGender("男");
        users.setMajor("政治学");
        boolean flag = usersService.register(users);
        //System.out.println(save);
        //成功，失败 ，true,false;
        Assert.isTrue(flag, "注册失败");
    }

    @Test
    public void testLogin() {
        Users users = usersService.login("张飞", "123456");
        Assert.notNull(users, "登录失败");
    }

    @Test
    public void testQueryAllUsers() {
        //System.out.println(usersService.queryAllUsers());
        List<Users> users = usersService.queryAllUsers();
        users.forEach(System.out::println);
    }

    @Test
    public void testGetUserById() {
        Users user = usersService.getUserById(4L);
        System.out.println(user);
    }

    @Test
    public void testUpdateUser() {
        Users users = new Users();
        users.setUid(6L);
        users.setUsername("刘备");
        users.setPassword("654321");
        users.setGender("男");
        users.setMajor("经济学");
        boolean flag = usersService.updateUser(users);
        Assert.isTrue(flag, "更新失败");
    }

    @Test
    public void testDeleteUserById() {
        boolean flag = usersService.deleteUserById(3L);
        Assert.isTrue(flag, "删除失败");
    }

    @Test
    public void testQueryUserByUsername() {
        List<Users> users = usersService.queryUserByUsername("张三丰");
        users.forEach(System.out::println);
    }

    @Test
    public void testAddUsersList(){

        for(int i=1;i<=100;i++){
            Users u = new Users();
            u.setUsername("4456787"+i+"@qq.com");
            u.setPassword("123456");
            u.setGender("男");
            u.setBirthday("2010-10-10");
            u.setMajor("计算机科学与技术");

            usersService.register(u);
        }
    }


    //测试分页的基本用法
    @Test
    public void testUsersPage() {
        //1 创建page对象
        //传入两个参数：当前页 和 每页显示记录数
        Page<Users> page = new Page<Users>(1, 10);
        //调用mp分页查询的方法
        //调用mp分页查询过程中，底层封装
        //把分页所有数据封装到page对象里面
        usersMapper.selectPage(page, null);
        page.getRecords().forEach(System.out::println);
        //通过page对象获取分页数据
        System.out.println(page.getCurrent());//当前页
        System.out.println(page.getRecords());//每页数据list集合
        System.out.println(page.getSize());//每页显示记录数
        System.out.println(page.getTotal()); //总记录数
        System.out.println(page.getPages()); //总页数
        System.out.println(page.hasNext()); //下一页
        System.out.println(page.hasPrevious()); //上一页
    }

    //测试批量删除
    @Test
    public void testDeleteBatchUsers() {
        //boolean flag = usersService.removeByIds(List.of(1L, 2L, 3L));
        List<Long> list = new ArrayList<Long>();
        list.add(131L);
        list.add(132L);
        list.add(133L);

        boolean flag = usersService.deleteBatchUsers(list);
        Assert.isTrue(flag, "批量删除失败");
    }
}
