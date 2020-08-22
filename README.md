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

