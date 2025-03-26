package com.bravos.steak.account.specifications;

import com.bravos.steak.account.entity.Account;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {
    public static Specification<Account> hasUsername(String username){
        return (root, query, cb) -> {
            return cb.equal(root.get("username"), username);
        };
    }
}
