package com.bravos.steak.account.service;

import com.bravos.steak.account.entity.AccountProfile;
import com.bravos.steak.account.model.response.AccountDTO;

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
     * Lấy AccountDTO bằng ID
     * @param id id
     * @return tài khoản
     */
    AccountDTO getAccountById(Long id);

    /**
     * Lấy AccountDTO bằng username
     * @param username username
     * @return tài khoản
     */
    AccountDTO getAccountByUsername(String username);

    /**
     * Lấy AccountDTO bằng email
     * @param email email
     * @return tài khoản
     */
    AccountDTO getAccountByEmail(String email);

    /**
     * Trả về profile của user
     * @param id id
     * @return profile của user
     */
    Optional<AccountProfile> getAccountProfileById(Long id);

}
