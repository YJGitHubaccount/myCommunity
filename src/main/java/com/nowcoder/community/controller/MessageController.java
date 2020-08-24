package com.nowcoder.community.controller;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping(path = "/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setPath("/letter/list");
        page.setLimit(10);
        page.setRows(messageService.findConversationCount(user.getId()));
        //最新的会话列表
        List<Message> conversationList  = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message conversation : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),conversation.getConversationId()));
                map.put("conversation",conversation);
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations",conversations);
        model.addAttribute("letterUnreadCount",messageService.findLetterUnreadCount(user.getId(),null));
        //写死
        model.addAttribute("noticeUnreadCount",10000);

        return "/site/letter";
    }

    @GetMapping(path = "letter/detail")
    public String getLetterDetail(){

        return "/site/letter-detail";
    }

    @GetMapping(path = "/notice/list")
    public String getNoticeList(){

        return "/site/notice";
    }

    @GetMapping(path = "/notice/detail")
    public String getNoticeDetail(){

        return "/site/notice-detail";
    }
}
