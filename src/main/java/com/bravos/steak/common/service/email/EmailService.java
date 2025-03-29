package com.bravos.steak.common.service.email;

import java.util.Map;

public interface EmailService {

    void sendEmailUsingTemplate(String to, String subject, String templateID, Map<String, Object> params);

}
