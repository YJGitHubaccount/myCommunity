package com.nowcoder.community;

import com.nowcoder.community.dao.*;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.util.CommunityConstant;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests implements CommunityConstant {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void testUser(){
        testInsertUser();
        testSelectUser();
        testUpdateUser();
    }

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

        user = userMapper.selectByName("zhangfei");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testDicussPostMapper(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testInsertDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(153);
        discussPost.setTitle("Test title");
        discussPost.setContent("Test content");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(1000);
        discussPost.setScore(100.0);
        System.out.println(discussPostMapper.insertDiscussPost(discussPost));
    }

    @Test
    public void testUpdateCommentCount(){
        discussPostMapper.updateCommentCount(109,100);
    }

    @Test
    public void testinsertComment(){
        Comment comment = new Comment();
        comment.setContent("Test");
        comment.setEntityId(276);
        comment.setCreateTime(new Date());
        comment.setEntityType(ENTITY_TYPE_POST);
        comment.setTargetId(0);
        comment.setStatus(0);
        comment.setUserId(153);
        commentMapper.insertComment(comment);
    }

    @Test
    public void testMessageMapper(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

        Message message = new Message();
        message.setStatus(0);
        message.setContent("Test11111111");
        message.setFromId(111);
        message.setToId(153);
        message.setCreateTime(new Date());
        message.setConversationId("111_153");
        messageMapper.insertMessage(message);

        List<Integer> ids = new ArrayList<>();
        for (int i=1;i<=5;i++){
            ids.add(i);
        }
        messageMapper.updateStatus(ids,0);

        System.out.println(messageMapper.selectLatestNotice(111,"like"));

        System.out.println(messageMapper.selectNoticeCount(111,"like"));

        System.out.println(messageMapper.selectNoticeUnreadCount(111,null));

        System.out.println(messageMapper.selectNoticeUnreadCount(111,"like"));

        List<Message> messages = messageMapper.selectNotices(111,"like",0,100);
        for (Message message1 : messages){
            System.out.println(message1);
        }

    }

}
