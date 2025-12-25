package org.example.server.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("commons")
public class CommonsController {


    @GetMapping("/hutool/randomCode")
    //生成验证码
    public void generateRandomCodeImage(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "token",required = true) String token) throws IOException {

        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 48,4,10);

        String randomCode = lineCaptcha.getCode();
        System.out.println("生成的验证码：" + randomCode);
        System.out.println("客户端的标识符："+token);

        //以客户端的标识符为键，验证码为值，key-value形式，验证保存服务器sesssion
        request.getSession().setAttribute(token,randomCode);

        lineCaptcha.write(response.getOutputStream());
    }
}
