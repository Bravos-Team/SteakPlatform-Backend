package com.bravos.steak.account.service.registration;

import com.bravos.steak.account.exception.EmailAlreadyExistException;
import com.bravos.steak.account.exception.UsernameAlreadyExistException;
import com.bravos.steak.account.exception.WeakPasswordException;
import com.bravos.steak.account.model.registration.RegistrationRequest;

public interface RegistrationService {

    /**
     * Xử lý yêu cầu đăng ký của người dùng, kiểm tra username, email, mật khẩu, nếu OKE thì gửi mail xác thực
     * @param registrationRequest yêu cầu đăng ký tài khoản
     * @return email dùng để đăng ký
     * @throws UsernameAlreadyExistException trùng username
     * @throws EmailAlreadyExistException trùng email
     */
    String preRegisterAccount(RegistrationRequest registrationRequest)
            throws UsernameAlreadyExistException, EmailAlreadyExistException, WeakPasswordException;

    

}
