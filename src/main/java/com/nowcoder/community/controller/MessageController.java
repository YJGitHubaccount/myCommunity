package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

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
        model.addAttribute("noticeUnreadCount",messageService.findNoticeUnreadCount(user.getId(),null));

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
    public String getNoticeList(Model model){
        int userId = hostHolder.getUser().getId();
        //查询评论类通知
        Message message = messageService.findLatestNotice(userId,TOPIC_COMMENT);
        if (message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(userId,TOPIC_COMMENT);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(userId,TOPIC_COMMENT);
            messageVO.put("unread",unread);

            model.addAttribute("commentNotice",messageVO);
        }

        //查询点赞类通知
        message = messageService.findLatestNotice(userId,TOPIC_LIKE);
        if (message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(userId,TOPIC_LIKE);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(userId,TOPIC_LIKE);
            messageVO.put("unread",unread);

            model.addAttribute("likeNotice",messageVO);
        }

        //查询关注类通知
        message = messageService.findLatestNotice(userId,TOPIC_FOLLOW);
        if (message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));

            int count = messageService.findNoticeCount(userId,TOPIC_FOLLOW);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(userId,TOPIC_FOLLOW);
            messageVO.put("unread",unread);

            model.addAttribute("followNotice",messageVO);
        }

        int letterUnreadCount = messageService.findLetterUnreadCount(userId,null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(userId,null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping(path = "/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        if (noticeList != null){
            for (Message notice : noticeList){
                Map<String,Object> map = new HashMap<>();
                //通知
                map.put("notice",notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知的作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                //未读通知的ids
                if (notice.getStatus() == 0) {
                    ids.add(notice.getId());
                }

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);


        //设置已读
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}
