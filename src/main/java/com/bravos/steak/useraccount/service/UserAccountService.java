package com.bravos.steak.useraccount.service;

import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.response.UserLoginResponse;

public interface UserAccountService {

    /**
     * Kiểm tra tài khoản tồn tại theo username hoặc email
     * @param username tên tài khoản
     * @param email i meow
     * @return true nếu tồn tại
     */
    boolean isExistByUsernameEmail(String username, String email);

    /**
     * Kiểm tra tài khoản tồn tại theo username
     * @param username tên tài khoản
     * @return true nếu tồn tại
     */
    boolean isExistByUsername(String username);

    /**
     * Kiểm tra tài khoản tồn tại theo email
     * @param email email
     * @return true nếu tồn tại
     */
    boolean isExistByEmail(String email);

    /**
     * Trả về profile của user
     * @param id id
     * @return profile của user
     */
    UserProfile getAccountProfileById(Long id);

    /**
     * Tìm account bằng username
     * @param username username
     * @return account tương ứng
     */
    UserAccount getAccountByUsername(String username);

    /**
     * Tìm account bằng email
     * @param email email
     * @return account tương ứng
     */
    UserAccount getAccountByEmail(String email);

    UserAccount getAccountById(Long id);

    UserLoginResponse getLoginResponseById(Long id);

}
