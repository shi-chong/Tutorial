package com.sc.config;

import com.sc.pojo.User;
import com.sc.service.UserService;
import jdk.nashorn.internal.parser.Token;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的UserRealm extends AuthorizingRealm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了----》授权");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermission("user:add");

        //拿到当前登录的这个对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser =(User) subject.getPrincipal();//拿到User对象
        //设置当前用户的权限
        info.addStringPermission(currentUser.getPerms());

        return info;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了----》认证");

        //用户名密码，密码 数据库中去取

        //连接真实数据库
        userService.queryUserByUserName("userToken.getUsername()");

        UsernamePasswordToken userToken=(UsernamePasswordToken) token;
        //连接真实数据库
        User user = userService.queryUserByUserName(userToken.getUsername());

        if(user==null){ //没有这人
            return null;
        }
        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute("loginUser",user);


        //可以加密：MD5 MD5盐值加密：MD5+自己的规则
        //密码认证，shiro来做
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}
