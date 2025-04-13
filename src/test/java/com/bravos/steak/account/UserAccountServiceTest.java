package com.bravos.steak.account;

import com.bravos.steak.account.service.impl.AccountServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
public class UserAccountServiceTest {

    @Autowired
    AccountServiceImpl accountService;

    @Test
    public void getAccountById(){
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e ->System.setProperty(e.getKey(),e.getValue()));
    }
}
