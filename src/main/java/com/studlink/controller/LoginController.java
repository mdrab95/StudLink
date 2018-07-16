package com.studlink.controller;

import com.studlink.model.User;
import com.studlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Login Controller
 */
@Controller
public class LoginController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @Autowired
    public LoginController(BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserService userService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    private String checkIfLogged(HttpSession ssn, String ifTrue, String ifFalse){
        if (ssn!=null && ssn.getAttribute("user") != null)
            return ifTrue;
        return ifFalse;
    }


    @RequestMapping("login")
    public String login(@ModelAttribute("user")User user) {
        return "login";
    }


    @RequestMapping("/access")
    public String access(HttpServletRequest request,
                         HttpServletResponse response,
                         @ModelAttribute("user")User user,
                         Model model) {

        User found = userService.findByEmail(user.getEmail());
        if (found != null && bCryptPasswordEncoder.matches(user.getPassword(), found.getPassword())) {
            request.getSession().setAttribute("user", found.getEmail());
            request.getSession().setAttribute("pass", found.getPassword());
            request.getSession().setAttribute("id", found.getId());
            return "home";
        }
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(@ModelAttribute("user")User user,
                         HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().setAttribute("user", null);
        request.getSession().setAttribute("pass", null);
        request.getSession().setAttribute("id", null);
        request.getSession().invalidate();
        return "index";
    }
}
