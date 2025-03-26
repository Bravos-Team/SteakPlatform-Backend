package com.bravos.steak.common.service.email.impl;

import com.bravos.steak.common.service.email.EmailService;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final MailjetClient mailClient;

    @Autowired
    public EmailServiceImpl(MailjetClient mailClient) {
        this.mailClient = mailClient;
    }

    @Override
    public void sendEmailUsingTemplate(String to, String subject, String templateID, Map<String, Object> params) {

        MailjetRequest request;
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "steak@bravos.io.vn")
                                        .put("Name", "Steak"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", to)))
                                .put(Emailv31.Message.TEMPLATEID, templateID)
                                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.VARIABLES, new JSONObject(params))));

        CompletableFuture.runAsync(() -> {
            try {
                MailjetResponse response = mailClient.postAsync(request).get();
                if(response.getStatus() == 200) {
                    log.info("Email was sent to {}", to);
                }
                else {
                    log.error(response.getData().toString());
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        });

    }


}
