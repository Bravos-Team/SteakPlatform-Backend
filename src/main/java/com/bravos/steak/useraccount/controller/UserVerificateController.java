package com.bravos.steak.useraccount.controller;

import com.bravos.steak.useraccount.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/verificate/user")
public class UserVerificateController {

    private final UserRegistrationService userRegistrationService;

    @Autowired
    public UserVerificateController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/{token}")
    public String verificateRegisterRequest(@PathVariable("token") String token) {
        try {
            userRegistrationService.postRegisterAccount(token);
            return "redirect:" + System.getProperty("BASE_URL_FRONTEND") + "/register-sucess";
        } catch (Exception e) {
            return "redirect:" + System.getProperty("BASE_URL_FRONTEND") + "/register-failed";
        }
    }

}
