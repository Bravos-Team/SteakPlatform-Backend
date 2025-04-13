package com.bravos.steak.account.service;

import com.bravos.steak.account.entity.UserAccount;
import com.bravos.steak.account.entity.UserProfile;

import java.util.Optional;

public interface AccountService {

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
    Optional<UserProfile> getAccountProfileById(Long id);

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
    Optional<UserAccount> getAccountById(Long id);

}
