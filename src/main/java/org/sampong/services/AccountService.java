package org.sampong.services;

import org.sampong.base.BaseService;
import org.sampong.models.Account;

import java.util.List;

public interface AccountService extends BaseService<Account> {
    List<Account> getAccountByUserId(Long userId);
}
