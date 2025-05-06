package com.bravos.steak.dev.controller;

import com.bravos.steak.dev.service.PublisherRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/verificate/dev")
public class PublisherVerificationController {

    private final PublisherRegistrationService publisherRegistrationService;

    public PublisherVerificationController(PublisherRegistrationService publisherRegistrationService) {
        this.publisherRegistrationService = publisherRegistrationService;
    }

    @GetMapping("/{token}")
    public String verificateRegisterRequest(@PathVariable("token") String token) {
        try {
            publisherRegistrationService.postRegisterPublisher(token);
            return "redirect:" + System.getProperty("BASE_URL_FRONTEND") + "/register-sucess";
        } catch (Exception e) {
            return "redirect:" + System.getProperty("BASE_URL_FRONTEND") + "/register-failed";
        }
    }

}
