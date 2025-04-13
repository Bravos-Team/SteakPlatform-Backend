package com.bravos.steak.account.specifications;

import com.bravos.steak.account.entity.UserAccount;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

    public static Specification<UserAccount> hasUsername(String username){
        return (root, query, cb) -> cb.equal(root.get("username"), username);
    }

}
