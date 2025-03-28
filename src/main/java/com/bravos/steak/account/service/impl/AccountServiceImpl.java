package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.model.mappers.AccountMapper;
import com.bravos.steak.account.model.response.AccountDTO;
import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.repo.AccountRepository;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.specifications.AccountSpecification;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    AccountMapper accountMapper;
    AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountDTO getAccountById(Long id) {
        return accountMapper
                .toAccountDTO(accountRepository.findById(id)
                        .orElseThrow( () -> new ResourceNotFoundException("Account with ID " + id + " not found")));
    }

    @Override
    @Transactional
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(accountMapper::toAccountDTO).toList();
    }


    @Override
    @Transactional
    public AccountDTO getAccountByUsername(String username){
        Specification<Account> spec = AccountSpecification.hasUsername(username);
        return accountMapper.toAccountDTO(
                accountRepository.findOne(spec).orElseThrow(() -> new ResourceNotFoundException("Account with username " + username + " not found"))
        );
    }

    @Override
    public boolean isExistByUsernameEmail(String username, String email) {
        return accountRepository.existsByUsernameAndEmail(username,email);
    }

}
