package com.bravos.steak.account.service;

import com.bravos.steak.account.model.request.EmailLoginRequest;
import com.bravos.steak.account.model.request.RefreshRequest;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;

public interface AuthService {

    LoginResponse login(UsernameLoginRequest usernameLoginRequest);

    LoginResponse login(EmailLoginRequest emailLoginRequest);

    LoginResponse renewToken(RefreshRequest refreshRequest);

}
