package com.bravos.steak.common.service.email;

import com.bravos.steak.common.model.EmailPayload;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EmailService {

    void sendEmailUsingTemplate(EmailPayload emailPayload);

}
