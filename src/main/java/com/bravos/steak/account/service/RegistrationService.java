package com.bravos.steak.account.service;

import com.bravos.steak.exceptions.AccountAlreadyExistsException;
import com.bravos.steak.account.model.request.RegistrationRequest;

public interface RegistrationService {

    /**
     * Xử lý yêu cầu đăng ký của người dùng, kiểm tra username, email, mật khẩu, nếu OKE thì gửi mail xác thực
     * @param registrationRequest yêu cầu đăng ký tài khoản
     * @return email dùng để đăng ký
     * @throws AccountAlreadyExistsException trùng username
     */
    String preRegisterAccount(RegistrationRequest registrationRequest)
            throws AccountAlreadyExistsException;

    /**
     * Xác minh email nhận và đăng ký tài khoản
     * @param token token để xác minh tài khoản
     * @return thành công hay thất bại
     */
    boolean verificateRegisterAccount(String token);

}
