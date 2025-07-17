package com.bravos.steak.common.service.email;

import com.bravos.steak.common.model.EmailPayload;

public interface EmailService {

    void sendEmailUsingTemplate(EmailPayload emailPayload);

}
