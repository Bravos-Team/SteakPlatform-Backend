package com.bravos.steak.account.service;

import com.bravos.steak.account.dto.mappers.AccountMapper;
import com.bravos.steak.account.dto.response.AccountDTO;
import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.repo.AccountRepository;
import com.bravos.steak.account.specifications.AccountSpecification;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountMapper accountMapper;
    AccountRepository accountRepository;

    @Transactional
    public AccountDTO getAccountById(Long id) {
        return accountMapper
                .toAccountDTO(accountRepository.findById(id)
                        .orElseThrow( () -> new ResourceNotFoundException("Account with ID " + id + " not found")));
    }

    @Transactional
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(accountMapper::toAccountDTO).toList();
    }


    @Transactional
    public AccountDTO getAccountByUsername(String username){
        Specification<Account> spec = AccountSpecification.hasUsername(username);
        return accountMapper.toAccountDTO(
                accountRepository.findOne(spec).orElseThrow(() -> new ResourceNotFoundException("Account with username " + username + " not found"))
        );
    }
}
