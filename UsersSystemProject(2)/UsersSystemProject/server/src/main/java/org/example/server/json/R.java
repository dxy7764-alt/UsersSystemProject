package org.example.server.json;

import lombok.Data;
import java.io.Serializable;

@Data
//R ===>Response 响应
public class R implements Serializable {

    private static final Integer SUCCESS = 200; //表示请求成功的状态码的常量
    private static final Integer FAIL = 400; //表示一般失败的状态码的常量
    private static final Integer ERROR = 500; //表示服务器内部错误的状态码常量

    private static final Integer UNAUTH = 403; // 没有权限

    private Integer code; //表示状态码属性
    private String msg; //表示响应的消息

    private Object data; //表示响应返回的数据
    private String token; //表示令牌，将来做身份认证用的，肯定能用到。

    //成功
    public static R success() {
        R r = new R();
        r.setCode(SUCCESS);
        r.setMsg("success");
        return r;
    }

    //方法重载...
    public static R success(String msg) {
        R r = new R();
        r.setCode(SUCCESS);
        r.setMsg(msg);
        return r;
    }

    public static R success(String msg, Object data) {
        R r = new R();
        r.setCode(SUCCESS);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static R success(String msg, Object data,String token) {
        R r = new R();
        r.setCode(SUCCESS);
        r.setMsg(msg);
        r.setData(data);
        r.setToken(token);
        return r;
    }

    //失败
    public static R fail() {
        R r = new R();
        r.setCode(FAIL);
        r.setMsg("failure");
        return r;
    }

    //方法重载...
    public static R fail(String msg) {
        R r = new R();
        r.setCode(FAIL);
        r.setMsg(msg);
        return r;
    }

    //错误
    //error

    public static R error() {
        R r = new R();
        r.setCode(ERROR);
        r.setMsg("error");
        return r;
    }

    //方法重载
    public static R error(String msg) {
        R r = new R();
        r.setCode(ERROR);
        r.setMsg(msg);
        return r;
    }

    public static R unauth() {
        R r = new R();
        r.setCode(UNAUTH);
        r.setMsg("unauthorized");
        return r;
    }

    public static R unauth(String msg) {
        R r = new R();
        r.setCode(UNAUTH);
        r.setMsg(msg);
        return r;
    }

    public static R fail(int code, String msg) {
        R r = new R();
        r.code = code;   // 传入403、500等状态码
        r.msg = msg;     // 传入提示语
        return r;
    }
}

