package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountRepositoryIF;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {
	@Autowired
	AccountRepositoryIF accountRepoIf;
	
	EmailNotificationService emailNotificationService = new EmailNotificationService();

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	// FundTransfer Code
	@Transactional(rollbackOn=Exception.class)
	public synchronized void depositFund(String fromAccountId, String toAccountId, BigDecimal amountToDeposit) throws Exception  {
		Account accountTobeDebited = this.accountRepoIf.findByAccountId(fromAccountId);
		Account accountToBeDeposited = this.accountRepoIf.findByAccountId(toAccountId);

		if (this.isBalanceNegative(accountTobeDebited, amountToDeposit) && (accountTobeDebited != null || accountToBeDeposited != null)) {
			accountTobeDebited.setBalance(accountTobeDebited.getBalance().subtract(amountToDeposit));
			accountToBeDeposited.setBalance(accountToBeDeposited.getBalance().add(amountToDeposit));
			
			this.accountRepoIf.save(accountTobeDebited);
			emailNotificationService.notifyAboutTransfer(accountTobeDebited, 
					"Amount Rs."+amountToDeposit+" transferred to account "+accountToBeDeposited.getAccountId() );
			
			this.accountRepoIf.save(accountToBeDeposited);
			emailNotificationService.notifyAboutTransfer(accountToBeDeposited, 
					"Your received amount Rs. "+amountToDeposit+" from account "+ accountTobeDebited.getAccountId());
		} else {
			System.out.println("Tranfer Fund Failed.Please check Logs.AccountTobeDebited= "+"accountTobeDebited"+", AccountToBeDeposited= "+accountToBeDeposited+", amountToDeposit= "+amountToDeposit);
			throw new Exception();
			
		}
	}

	private boolean isBalanceNegative(Account acct, BigDecimal amountToDeposit) {
		return (acct.getBalance().subtract(amountToDeposit).intValue() >= 0) ? true : false;
	}
}
