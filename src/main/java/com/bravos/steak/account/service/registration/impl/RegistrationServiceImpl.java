package com.bravos.steak.account.service.registration.impl;

import com.bravos.steak.account.exception.EmailAlreadyExistException;
import com.bravos.steak.account.exception.UsernameAlreadyExistException;
import com.bravos.steak.account.exception.WeakPasswordException;
import com.bravos.steak.account.model.registration.RegistrationRequest;
import com.bravos.steak.account.service.registration.RegistrationService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Override
    public String preRegisterAccount(RegistrationRequest registrationRequest) throws
            UsernameAlreadyExistException,
            EmailAlreadyExistException,
            WeakPasswordException {



        return "";
    }

}
