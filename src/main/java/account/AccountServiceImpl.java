package account;

import utils.AccountValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Account createAccount(String name) {
        AccountValidationUtils.validateAccountName(name);
        return accounts.computeIfAbsent(name.toLowerCase(), Account::new);
    }

    @Override
    public Account getAccount(String name) {
        return accounts.computeIfAbsent(name.toLowerCase(), Account::new);
    }

    @Override
    public void updateAccount(Account account) {
        accounts.put(account.getName().toLowerCase(), account);
    }

}