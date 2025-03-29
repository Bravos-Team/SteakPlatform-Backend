package com.bravos.steak.account.model.mappers;

import com.bravos.steak.account.model.response.AccountDTO;
import com.bravos.steak.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "status", expression = "java(account.getStatus() != null ? account.getStatus().name() : null)")
    AccountDTO toAccountDTO(Account account);

    Account toAccount(AccountDTO accountDTO);

}
