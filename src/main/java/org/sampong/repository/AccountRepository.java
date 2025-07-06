package org.sampong.repository;

import org.sampong.base.BaseRepository;
import org.sampong.models.Account;

import java.util.List;

public interface AccountRepository extends BaseRepository<Account> {
    Boolean deposit(Long accId, Double amount);

    Boolean withdraw(Long accId, Double amount);

    Boolean transfer(Long srcAccId, Long destAccId, Double amount);

    Boolean checkUserAndAccount(Long userId, Long accountId, String accountNumber);

    Object getAccountLock(Long accountId);

    List<Account> getAccountsByUserId(Long userId);

}
