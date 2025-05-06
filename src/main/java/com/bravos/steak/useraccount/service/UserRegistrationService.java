package com.bravos.steak.useraccount.service;

import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.useraccount.model.request.UserRegistrationRequest;

public interface UserRegistrationService {

    /**
     * Xử lý yêu cầu đăng ký của người dùng, kiểm tra username, email, mật khẩu, nếu OKE thì gửi mail xác thực
     * @param userRegistrationRequest yêu cầu đăng ký tài khoản
     * @return email dùng để đăng ký
     * @throws ConflictDataException trùng username
     */
    void preRegisterAccount(UserRegistrationRequest userRegistrationRequest);

    /**
     * Xác minh email nhận và đăng ký tài khoản
     * @param token token để xác minh tài khoản
     */
    void postRegisterAccount(String token);

}
