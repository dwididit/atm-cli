package account;

import java.math.BigDecimal;

public abstract class BaseAccount {
    protected BigDecimal balance;

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds\n");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void transfer(Account target, BigDecimal amount) {
        this.withdraw(amount);
        target.deposit(amount);
    }
}
