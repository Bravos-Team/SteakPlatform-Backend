package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.UserAccount;
import com.bravos.steak.account.entity.UserProfile;
import com.bravos.steak.account.repo.UserAccountRepository;
import com.bravos.steak.account.repo.UserProfileRepository;
import com.bravos.steak.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountServiceImpl implements AccountService {

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
    public Optional<UserProfile> getAccountProfileById(Long id) {
        return userProfileRepository.findById(id);
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
    public Optional<UserAccount> getAccountById(Long id){return userAccountRepository.findById(id);}
}
