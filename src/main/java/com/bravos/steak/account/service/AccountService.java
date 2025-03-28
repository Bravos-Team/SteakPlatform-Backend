package com.bravos.steak.account.service;

import com.bravos.steak.account.model.response.AccountDTO;

import java.util.List;

public interface AccountService {

    /**
     * Lấy tài khoản bằng ID
     * @param id id
     * @return Account
     */
    AccountDTO getAccountById(Long id);

    List<AccountDTO> getAllAccounts();

    AccountDTO getAccountByUsername(String username);

    boolean isExistByUsernameEmail(String username, String email);




}
