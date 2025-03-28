package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.response.AccountDTO;
import com.bravos.steak.account.model.response.ResponseObject;
import com.bravos.steak.account.service.impl.AccountServiceImpl;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/account")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AccountController {

    AccountServiceImpl accountService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
       return ResponseEntity.ok(
               new ResponseObject<>("success","Account found successfully",accountService.getAccountById(id))
       );
    }

    @GetMapping("")
    public ResponseEntity<?> getAllAccounts(@RequestParam(required = false) String username){
        if(username != null){
            return ResponseEntity.ok(
                    new ResponseObject<>("success","Account found successfully",accountService.getAccountByUsername(username))
            );
        }
        List<AccountDTO> list = accountService.getAllAccounts();
        if(list.isEmpty()) throw new ResourceNotFoundException("No accounts found");
        return ResponseEntity.ok(
                new ResponseObject<>("success","Accounts found successfully",list)
        );
    }


}
