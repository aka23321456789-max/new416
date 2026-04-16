package com.aka.news.config;

import com.aka.news.utils.JwtUtil;
import com.aka.news.utils.ThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestUri = request.getRequestURI();
        System.out.println("Request URI: " + requestUri);
        
        // 排除登录和注册接口
        if (requestUri.equals("/user/login") || requestUri.equals("/user/add")) {
            System.out.println("Excluded path, allowing access");
            return true;
        }

        // 验证token
        String token = request.getHeader("Authorization");//获取请求头中的token
        System.out.println("Token: " + token);
        try{
            Map<String, Object> claims = JwtUtil.parseToken(token);//解析token

            ThreadLocalUtil.set(claims);//存储claims，用于后续的权限验证
            return true;


        } catch (Exception e){
            System.out.println("Token validation failed: " + e.getMessage());//验证失败
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求处理完成后清理 ThreadLocal 中的数据，以避免内存泄漏
        ThreadLocalUtil.remove();
    }
}
