package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.Session;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @GetMapping(path = "/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping(path = "/login")
    public String getLoginPage(){
        return "/site/login";
    }

    /**
     * 提交注册表单，自动对应生成user
     * @param model
     * @param user
     * @return
     */
    @PostMapping(path = "/register")
    public String registerUser(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            //success
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target","/index");//跳转页面
            return "/site/operate-result";
        }else{
            //fail
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活账号
     * @param model
     * @param userId
     * @param code
     * @return
     */
    // http://localhost:8080/community/activation/101/code
    @GetMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用了!");
            model.addAttribute("target","/login");//跳转页面
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活过了!");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败!");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    /**
     * 浏览器访问此路径申请验证码
     * @param response
     * @param session
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}