package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(Model model, @PathVariable int userId){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        return "/site/profile";
    }

    @LoginRequired
    @GetMapping(path = "/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping(path = "/upload")
    public String uploadHeader(MultipartFile headerImage,Model model){
        if (headerImage == null){
            model.addAttribute("error","你还没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败!" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常",e);
        }

        //更新当前用户头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    /**
     * 浏览器获取头像
     * @param fileName
     * @param response
     */
    @GetMapping(path = "/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("imgae/" + suffix);
        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                ){
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping(path = "/password")
    public String changePassword(Model model, String oldPassword, String newPassword,String confirmPassword){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg","user不存在，修改密码失败");
            return "/site/operate-result";
        }
        //验证旧密码
        if (user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))){
            //验证新密码
            if (newPassword.equals(confirmPassword)){
                if (userService.updatePassword(user.getId(),newPassword) == 1){
                    model.addAttribute("msg","修改密码成功");
                    model.addAttribute("target","/index");
                    return "/site/operate-result";
                }
                else{
                    model.addAttribute("msg","修改密码失败,请重新再试");
                    model.addAttribute("target","/user/setting");
                    return "/site/operate-result";
                }
            }
            else {
                model.addAttribute("confirmError","两次输入的密码不一致!");
                return "/site/setting";
            }
        }
        else {
            model.addAttribute("oldError","旧密码错误!");
            return "/site/setting";
        }
    }
}
