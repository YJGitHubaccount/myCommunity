### day 2020/8/21
修改.gitignore没起效
git rm -r --cached .    //删除项目缓存即可

### day 2020/8/22
int DEFAULT_EXPIRED_SECONDS = 3600 * 12;//12小时
int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;//100天
在
loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
这里超过了int大小上限，所以改为了long

退出登录页面路径
index.html处修改
"/logout"(出现匹配错误变成"/login?logout") --> "/out"

### day 2020/8/24
发布帖子使用了异步请求,配合弹出显示框
发布评论回复是普通的post请求

letter.html -> 使用了码云版本
notice.html -> 使用了码云版本
notice-detail.html -> 使用了码云版本
letter-detail.html -> 使用了码云版本


### day 2020/8/25
- 只有登录的人可以点赞,否则会报错

- profile.html 我的帖子 我的回复 链接没有设置

- index.html 消息 -> th:text="${allUnreadCount!=0?allUnreadCount:''}" 还未更改 --> √


### day 2020/8/26
- zookeeper,kafka要命令行开启才能用
- bin\windows\zookeeper-server-start.bat config\zookeeper.properties
- bin\windows\kafka-server-start.bat config\server.properties


### day 2020/8/27
- 朋友私信处用户头像的链接 --> √
- elasticsearch 7.9.0
- java 14 -> 12
