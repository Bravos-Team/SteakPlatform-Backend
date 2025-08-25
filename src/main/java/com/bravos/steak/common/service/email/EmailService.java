package com.bravos.steak.common.service.email;

import com.bravos.steak.common.model.EmailPayload;

public interface EmailService {

    void sendEmail(EmailPayload emailPayload);

}
