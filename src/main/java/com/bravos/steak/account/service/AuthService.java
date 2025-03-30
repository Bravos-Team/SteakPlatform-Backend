package com.bravos.steak.account.service;

import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;

public interface AuthService {

    LoginResponse login(UsernameLoginRequest usernameLoginRequest);

    String renewToken(String refreshToken);

}
