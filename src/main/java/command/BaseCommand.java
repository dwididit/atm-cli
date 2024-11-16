package command;

import session.SessionService;

import java.math.BigDecimal;

public abstract class BaseCommand {
    protected void checkLoggedIn(SessionService sessionService) {
        String currentUser = sessionService.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new IllegalStateException("No user logged in\n");
        }
    }

    protected void validateArgs(String... args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments cannot be null or empty\n");
        }
        for (String arg : args) {
            if (arg == null || arg.trim().isEmpty()) {
                throw new IllegalArgumentException("Arguments cannot be null or empty\n");
            }
        }
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive\n");
        }
    }
}