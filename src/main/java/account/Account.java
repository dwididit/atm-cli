package account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account extends BaseAccount {
    private String name;

    public Account(String name) {
        this.name = name.toLowerCase();
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
