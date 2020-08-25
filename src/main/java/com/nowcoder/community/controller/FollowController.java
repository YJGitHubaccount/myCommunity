package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FollowController {

    @Autowired
    private UserService userService;


    @GetMapping(path = "/followees/{userId}")
    public String getFollowersPage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        //分页
        page.setRows(10);//写死
        page.setLimit(5);
        page.setPath("/followees/" + userId);

        return "/site/follower";
    }
}
