package com.aka.news.controller;

import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.UserService;
import com.aka.news.utils.JwtUtil;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public Result addUser(@RequestBody user user){
        try {
            // 参数验证
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.fail("用户名不能为空");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.fail("密码不能为空");
            }
            //查重
            List<user> users = userService.list(new QueryWrapper<user>().eq("username", user.getUsername()));//查询用户名
            if (users.size() > 0) {
                return Result.fail("用户名已存在");
            }
            
            user.setRole(1);//设置角色为普通用户
            userService.save(user);  //使用 MyBatis-Plus 的 save 方法
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("注册失败: " + e.getMessage());
        }
    }

    @RequestMapping("/login")
    public Result login(@RequestBody user user){

        try {
            String username = user.getUsername();
            String password = user.getPassword();

            List<user> users = userService.list(new QueryWrapper<user>()
                    .eq("username", username)
                    .eq("password", password));

            if (users.size() == 0) {
                return Result.fail("用户名或密码错误");
            }

            // 假设验证通过
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            String token = JwtUtil.genToken(claims);
            System.out.println("Generated token: " + token);
            return Result.success(token);//返回 token给客户端

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return Result.fail("登录失败: " + e.getMessage());
        }
    }


    @GetMapping("/test")
    public int test(){
        return 991119;

    }

    @PutMapping("/update")
    public Result updateUser(@RequestBody Map<String, Object> updates){
        try{

            Map<String, Object> map = ThreadLocalUtil.get();
            String name = (String) map.get("username");
            user user = userService.getOne(new QueryWrapper<user>().eq("username", name));//查询到登录用户
            if(user == null) return Result.fail("用户登录状态异常，请重新登陆");
            //改数据库值
            //检查用户名是否重复,添加
            if(updates.containsKey("username")){
                List<user> users = userService.list(new QueryWrapper<user>().
                        eq("username", updates.get("username")));
                if (users.size() > 0 && !users.get(0).getUsername().equals(name)) {
                    return Result.fail("用户名已存在");
                }
                user.setUsername(updates.get("username").toString());//更改用户名
            }
            if(updates.containsKey("password"))
                user.setPassword(updates.get("password").toString());//更改密码
            if(updates.containsKey("phone"))
                user.setPhone(updates.get("phone").toString());//更改手机号
            userService.updateById(user);  //使用 MyBatis-Plus 的 updateById 方法更新用户信息
            //更改ThreadLocal中的数据
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            ThreadLocalUtil.set(claims);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新失败: " + e.getMessage());
        }
    }




}
