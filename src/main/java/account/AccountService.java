package account;

public interface AccountService {
    Account createAccount(String name);
    Account getAccount(String name);
    void updateAccount(Account account);
    boolean accountExists(String name);
}