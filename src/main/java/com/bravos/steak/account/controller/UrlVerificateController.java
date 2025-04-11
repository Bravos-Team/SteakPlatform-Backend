package com.bravos.steak.account.controller;

import com.bravos.steak.account.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class UrlVerificateController {

    private final RegistrationService registrationService;

    public UrlVerificateController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    private final String domain = System.getProperty("BASE_URL_FRONTEND");

    @GetMapping("/verificate/{token}")
    public String verificateRegisterRequest(@PathVariable("token") String token) {
        try {
            registrationService.verificateRegisterAccount(token);
            return "redirect:" + domain + "/register-sucess";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:" + domain + "/register-failed";
        }
    }

}
