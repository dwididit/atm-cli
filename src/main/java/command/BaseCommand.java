package command;

import session.SessionService;

import java.math.BigDecimal;

public abstract class BaseCommand {
    protected void checkLoggedIn(SessionService sessionService) {
        if (!sessionService.isLoggedIn()) {
            throw new IllegalStateException("No user logged in");
        }
    }

    protected void validateArgs(String... args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments cannot be empty");
        }
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
    }
}