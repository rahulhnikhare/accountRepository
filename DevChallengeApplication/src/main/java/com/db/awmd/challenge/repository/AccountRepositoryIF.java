package com.db.awmd.challenge.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;

@Repository
@Transactional(rollbackOn=Exception.class)
public interface AccountRepositoryIF extends CrudRepository<Account, Integer> {

	public Account findByAccountId(String accountId);
}
