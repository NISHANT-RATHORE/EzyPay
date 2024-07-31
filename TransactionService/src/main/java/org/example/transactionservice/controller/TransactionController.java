package org.example.transactionservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.TransactionServiceApplication;
import org.example.transactionservice.dto.InitiateTrxnRequest;
import org.example.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public String intiateTransaction(@RequestBody @Valid InitiateTrxnRequest request){
        log.info("Transaction Controller invoked");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String senderPhoneno = userDetails.getUsername();
        return transactionService.initiateTransaction(senderPhoneno,request);
    }
}
