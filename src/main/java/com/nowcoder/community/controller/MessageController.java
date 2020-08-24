package com.nowcoder.community.controller;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

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

    @GetMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        //分页
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        System.out.println(page.getCurrent());
        System.out.println(page.getLimit());

        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message : letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target",getLetterTarget(conversationId));

        //更新已读
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    public User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }
        else{
            return userService.findUserById(id0);
        }
    }

    @PostMapping(path = "/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if (target == null){
            return CommunityUtil.getJsonString(1,"目标用户不存在!");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+ "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId()+ "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    @GetMapping(path = "/notice/list")
    public String getNoticeList(Page page){
        //分页信息
        page.setPath("/notice/list");
        page.setLimit(5);
        //page.setRows();


        return "/site/notice";
    }

    @GetMapping(path = "/notice/detail")
    public String getNoticeDetail(){

        return "/site/notice-detail";
    }
}
