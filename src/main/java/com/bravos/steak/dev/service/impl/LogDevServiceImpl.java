package com.bravos.steak.dev.service.impl;

import com.bravos.steak.dev.service.LogDevService;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

@Service
public class LogDevServiceImpl implements LogDevService {

    @Override
    public void saveLog(long devId, long publisherId, String message, Object... args) {
        String finalMessage = format(message,args);
        sendLog();
    }

    private String format(String template, Object... args) {
        for (Object arg : args) {
            template = template.replaceFirst("\\{}", Matcher.quoteReplacement(String.valueOf(arg)));
        }
        return template;
    }

    private void sendLog() {

    }

}
