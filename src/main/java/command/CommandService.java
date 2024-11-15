package command;

import java.math.BigDecimal;

public interface CommandService {
    void login(String name);
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    void transfer(String target, BigDecimal amount);
    void logout();
}
