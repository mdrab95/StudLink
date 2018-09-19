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
 *  General Controller - defines all waypoints (except login and register)
 */
@Controller
public class GeneralController {

    private String checkIfLogged(HttpSession ssn, String ifTrue, String ifFalse) {
        if (ssn != null && ssn.getAttribute("user") != null)
            return ifTrue;
        return ifFalse;
    }

    @RequestMapping("/")
    public String nothing(HttpServletRequest request,
                          HttpServletResponse response,
                          @ModelAttribute("user") User user,
                          Model model) {
        return checkIfLogged(request.getSession(), "home", "index");
    }

    @RequestMapping("home")
    public String home(HttpServletRequest request,
                       HttpServletResponse response,
                       @ModelAttribute("user") User user,
                       Model model) {
        return checkIfLogged(request.getSession(), "home", "index");
    }

    @RequestMapping("index")
    public String index(HttpServletRequest request,
                        HttpServletResponse response,
                        @ModelAttribute("user") User user,
                        Model model) {
        return checkIfLogged(request.getSession(), "home", "index");
    }

    @RequestMapping("/api/id")
    public String userId() {
        return "id";
    }

}
