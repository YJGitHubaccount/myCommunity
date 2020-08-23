package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/add")
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJsonString(403,"你还没有登录");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setScore((double) 0);
        discussPost.setCommentCount(0);
        discussPostService.addDiscussPost(discussPost);

        //报错情况将来统一处理
        return CommunityUtil.getJsonString(0,"发布成功!");
    }
}
