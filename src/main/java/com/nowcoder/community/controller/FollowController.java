package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        int userId = hostHolder.getUser().getId();
        followService.follow(userId,entityType,entityId);

        return CommunityUtil.getJsonString(0,"已关注！");
    }

    @PostMapping(path = "/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        int userId = hostHolder.getUser().getId();
        followService.unfollow(userId,entityType,entityId);

        return CommunityUtil.getJsonString(0,"已取消关注！");
    }

    @GetMapping(path = "/followees/{userId}")
    public String getFolloweesPage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);
        //分页
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        page.setLimit(5);
        page.setPath("/followees/" + userId);

        List<Map<String,Object>> userList  = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if (userList != null) {
            for (Map<String,Object> map : userList){
                User u = (User) map.get("user");
                if (hostHolder.getUser() == null){
                    map.put("hasFollowed",false);
                }
                map.put("hasFollowed",followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,u.getId()));
            }
        }

        model.addAttribute("followees",userList);

        return "/site/followee";
    }

    @GetMapping(path = "/followers/{userId}")
    public String getFollowersPage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        //分页
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        page.setLimit(5);
        page.setPath("/followees/" + userId);

        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if (userList != null){
            for (Map<String,Object> map : userList){
                User u = (User) map.get("user");
                if (hostHolder.getUser() == null){
                    map.put("hasFollowed",false);
                }
                assert (u != null);
                map.put("hasFollowed",followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,u.getId()));
            }
        }

        model.addAttribute("followers",userList);

        return "/site/follower";
    }
}
