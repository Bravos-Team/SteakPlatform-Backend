package com.bravos.steak.common.service.email;

import com.bravos.steak.common.model.EmailPayload;

import java.util.Map;

public interface EmailService {

    void sendEmailUsingTemplate(EmailPayload emailPayload);

}
