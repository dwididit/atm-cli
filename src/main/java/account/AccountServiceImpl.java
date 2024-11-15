package account;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Account createAccount(String name) {
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

    @Override
    public boolean accountExists(String name) {
        return accounts.containsKey(name.toLowerCase());
    }
}