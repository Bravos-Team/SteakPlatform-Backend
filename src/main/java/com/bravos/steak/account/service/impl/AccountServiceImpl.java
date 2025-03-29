package com.bravos.steak.account.service.impl;

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
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ProfileRepository profileRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public boolean isExistByUsernameEmail(String username, String email) {
        return accountRepository.existsByUsernameAndEmail(username,email);
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
    public AccountDTO getAccountById(Long id) {
        return accountMapper.toAccountDTO(accountRepository.findById(id).orElse(null));
    }

    @Override
    public AccountDTO getAccountByUsername(String username) {
        return accountMapper.toAccountDTO(accountRepository.findByUsername(username));
    }

    @Override
    public AccountDTO getAccountByEmail(String email) {
        return accountMapper.toAccountDTO(accountRepository.findByEmail(email));
    }

    @Override
    public Optional<AccountProfile> getAccountProfileById(Long id) {
        return profileRepository.findById(id);
    }

}
