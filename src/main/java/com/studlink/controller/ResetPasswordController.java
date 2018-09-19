package com.studlink.controller;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studlink.model.User;
import com.studlink.service.EmailService;
import com.studlink.service.UserService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

/**
 * Reset password process
 */
@Controller
public class ResetPasswordController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;

    @Autowired
    public ResetPasswordController(BCryptPasswordEncoder bCryptPasswordEncoder,
                              UserService userService, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Return reset form template
    @RequestMapping(value="/reset", method = RequestMethod.GET)
    public ModelAndView showResetPasswordPage(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
        modelAndView.setViewName("reset");
        return modelAndView;
    }

    // Process form input data
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ModelAndView processResetPasswordPage(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request) {

        // Lookup user in database by e-mail
        User userExists = userService.findByEmail(user.getEmail());

        System.out.println(userExists);

        if (userExists != null) {
            // Generate random 36-character string token for confirmation link
            userExists.setConfirmationToken(UUID.randomUUID().toString());
            userService.saveUser(userExists);
            String appUrl = request.getScheme() + "://" + request.getServerName();

            SimpleMailMessage resetPasswordEmail = new SimpleMailMessage();
            resetPasswordEmail.setTo(userExists.getEmail());
            resetPasswordEmail.setSubject("Reset your studlink password");
            resetPasswordEmail.setText("Hello " + user.getFirstName() + ",\n"
                    + "To reset your password, please click the link below:\n"
                    + appUrl + ":8080/new_password?token=" + userExists.getConfirmationToken()
                    + "\nIf you didn't change your password, please contact with our support. \n "
                    + "\nThis is an automated email. Please do not reply to it. ");
            resetPasswordEmail.setFrom("studlink.confirmation@gmail.com");

            emailService.sendEmail(resetPasswordEmail);

            modelAndView.addObject("confirmationMessage", "Check your email.");
        }

        modelAndView.setViewName("checkyouremail");

        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value="/new_password", method = RequestMethod.GET)
    public ModelAndView setNewPassword(ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB
            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        modelAndView.setViewName("new_password");
        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value="/new_password", method = RequestMethod.POST)
    public ModelAndView setNewPassword(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

        modelAndView.setViewName("new_password");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(requestParams.get("password"));

        if (strength.getScore() < 3) {
            //modelAndView.addObject("errorMessage", "Your password is too weak.  Choose a stronger one.");
            bindingResult.reject("password");

            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");

            modelAndView.setViewName("redirect:new_password?token=" + requestParams.get("token"));
            System.out.println(requestParams.get("token"));
            return modelAndView;
        }

        // Find the user associated with the reset token
        User user = userService.findByConfirmationToken(requestParams.get("token"));

        // Set new password
        user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));


        // Save user
        userService.saveUser(user);

        SimpleMailMessage resetPasswordEmailConfirmation = new SimpleMailMessage();
        resetPasswordEmailConfirmation.setTo(user.getEmail());
        resetPasswordEmailConfirmation.setSubject("Your Studlink password has been changed");
        resetPasswordEmailConfirmation.setText("Hello " + user.getFirstName() + ",\n"
                + "Your Studlink password has been changed. You can use your new password the next time you login in.\n"
                + "If you didn't change your password, please contact with our support. \n "
                + "\nThis is an automated email. Please do not reply to it. ");
        resetPasswordEmailConfirmation.setFrom("studlink.confirmation@gmail.com");

        emailService.sendEmail(resetPasswordEmailConfirmation);

        modelAndView.addObject("successMessage", "Your password has been set!");
        return modelAndView;
    }
}
