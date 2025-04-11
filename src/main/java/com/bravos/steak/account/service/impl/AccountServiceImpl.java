package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.entity.AccountProfile;
import com.bravos.steak.account.model.mappers.AccountMapper;
import com.bravos.steak.account.model.response.AccountDTO;
import com.bravos.steak.account.repo.AccountRepository;
import com.bravos.steak.account.repo.ProfileRepository;
import com.bravos.steak.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ProfileRepository profileRepository) {
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean isExistByUsernameEmail(String username, String email) {
        return accountRepository.existsByUsernameOrEmail(username,email);
    }

    @Override
    public boolean isExistByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public boolean isExistByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public Optional<AccountProfile> getAccountProfileById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> getAccountById(Long id){return accountRepository.findById(id);}
}
