package com.second.jtrace.server.controller;

import com.second.jtrace.core.util.StringUtils;
import com.second.jtrace.server.dto.LoginRequest;
import com.second.jtrace.server.dto.Result;
import com.second.jtrace.server.dto.UserDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/user")
public class UserController {

    //Post请求
    @PostMapping("login")
    public Result login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        System.out.println("loginRequest: " + loginRequest);
        // TODO: check username and password
        UserDTO user = new UserDTO();
        user.setUsername(loginRequest.getUsername());
        user.setSessionId(StringUtils.UUID(true));
        session.setAttribute("user", user);
        return Result.ok(user);
    }

    @RequestMapping("logout")
    public String logout() {
        return "logout success";
    }
}
