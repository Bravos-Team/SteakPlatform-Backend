package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.PublisherRegistrationRequest;

public interface PublisherRegistrationService {

    /**
     * Kiểm tra trùng lặp dữ liệu và tạo token đăng ký (cái token này dùng để gửi email)
     * @param publisherRegistrationRequest request đăng ký
     * @return token
     */
    void preRegisterPublisher(PublisherRegistrationRequest publisherRegistrationRequest);

    /**
     * Xác thực email
     * @param token token
     */
    void postRegisterPublisher(String token);

}
