package com.bravos.steak.account.controller;

import com.bravos.steak.account.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UrlVerificateController {

    private final RegistrationService registrationService;

    public UrlVerificateController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/verificate/{token}")
    public String verificateRegisterRequest(@PathVariable("token") String token) {
        try {
            registrationService.verificateRegisterAccount(token);
            return "redirect:";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
