### day 2020/8/20
删除了index.html一些还未用到的数据模型
√ 已经添加回去了

### day 2020/8/21
修改.gitignore没起效
git rm -r --cached .    //删除项目缓存即可

### day /2020/8/22
```
<div class="col-sm-4">
    <img th:src="@{/kaptcha}" id="kaptcha" style="width:100px;height:40px;" class="mr-2"/>
    <a href="javascript:refresh_kaptcha();" class="font-size-12 align-bottom">刷新验证码</a>
</div>
```
修改为
```
<div class="col-sm-4">
    <img th:src="@{/img/captcha.png}" id="kaptcha" style="width:100px;height:40px;" class="mr-2"/>
    <a href="javascript:refresh_kaptcha();" class="font-size-12 align-bottom">刷新验证码</a>
</div>
```

