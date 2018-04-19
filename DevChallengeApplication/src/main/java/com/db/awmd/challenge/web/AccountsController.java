package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountRepositoryIF;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  
  private final AccountRepositoryIF accountRepositoryIF;
  

  @Autowired
  public AccountsController(AccountsService accountsService,
		  AccountRepositoryIF accountRepositoryIF) {
    this.accountsService = accountsService;
    this.accountRepositoryIF = accountRepositoryIF;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    
    Account acct =  this.accountsService.getAccount(accountId);
    return this.accountsService.getAccount(accountId);
  }

  @PutMapping(path="/fundTransfer/{fromAccountId}/{toAccountId}/{amount}")
  public ResponseEntity<Object> fundTransfer
  		 (@PathVariable("fromAccountId") String fromAccountId,
		  @PathVariable("toAccountId") String toAccountId,
		  @PathVariable("amount") @NotNull  String amount) {
 
		try {
			if(Integer.valueOf(amount) >0)
				this.accountsService.depositFund(fromAccountId, toAccountId, new BigDecimal(amount));
			else {
				return new ResponseEntity<Object>("Negative Amount", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			System.out.println(ex);
			return new ResponseEntity<Object>("Error Occured in Fund TRansfer", HttpStatus.BAD_REQUEST);
		}
	  
		return new ResponseEntity<>(HttpStatus.CREATED);
  }
  
  @GetMapping(path = "/test")
  public String getTestAccount() {
    System.out.println("IN TEST");
    
    for(int i=0;i<=10;i++) {
		  this.accountRepositoryIF.save(new Account(""+i+1));
	  }
    return "TEST";
  }

}
