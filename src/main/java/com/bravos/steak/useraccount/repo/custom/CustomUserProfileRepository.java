package com.bravos.steak.useraccount.repo.custom;

import com.bravos.steak.useraccount.model.response.UserLoginResponse;

public interface CustomUserProfileRepository {

    UserLoginResponse findLoginResponseById(Long id);

}
