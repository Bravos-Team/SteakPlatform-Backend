package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.response.UserLoginResponse;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public boolean isExistByUsernameEmail(String username, String email) {
        return userAccountRepository.existsByUsernameOrEmail(username,email);
    }

    @Override
    public boolean isExistByUsername(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    @Override
    public boolean isExistByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    @Override
    public UserProfile getAccountProfileById(Long id) {
        return userProfileRepository.findById(id).orElse(null);
    }

    @Override
    public UserAccount getAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public UserAccount getAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    public UserAccount getAccountById(Long id){return userAccountRepository.findById(id).orElse(null);}

    @Override
    public UserLoginResponse getLoginResponseById(Long id) {
        return userProfileRepository.findLoginResponseById(id);
    }

}
