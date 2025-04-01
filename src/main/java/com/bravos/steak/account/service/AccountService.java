package com.bravos.steak.account.service;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.entity.AccountProfile;

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
    Optional<AccountProfile> getAccountProfileById(Long id);

    /**
     * Tìm account bằng username
     * @param username username
     * @return account tương ứng
     */
    Account getAccountByUsername(String username);

    /**
     * Tìm account bằng email
     * @param email email
     * @return account tương ứng
     */
    Account getAccountByEmail(String email);
    Optional<Account> getAccountById(Long id);
}
