package com.studlink.controller;

import com.studlink.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Ace on 18.07.2018.
 */
@Controller
public class ProfileController {

    private String checkIfLogged(HttpSession ssn, String ifTrue, String ifFalse) {
        if (ssn != null && ssn.getAttribute("user") != null)
            return ifTrue;
        return ifFalse;
    }

    @RequestMapping("/profile")
    public String profile(HttpServletRequest request,
                          HttpServletResponse response,
                          @ModelAttribute("user") User user,
                          Model model) {
        return checkIfLogged(request.getSession(), "profile", "login");
    }
}
