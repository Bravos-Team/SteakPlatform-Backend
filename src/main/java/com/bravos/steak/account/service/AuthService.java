package com.bravos.steak.account.service;

import com.bravos.steak.account.model.request.LoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    String renewToken(String refreshToken);

}
