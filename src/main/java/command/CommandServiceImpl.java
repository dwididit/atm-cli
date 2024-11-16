package command;

import lombok.RequiredArgsConstructor;
import service.BankService;
import session.SessionService;
import utils.AccountValidationUtils;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CommandServiceImpl extends BaseCommand implements CommandService {
    private final BankService bankService;
    private final SessionService sessionService;

    @Override
    public void login(String name) {
        AccountValidationUtils.validateAccountName(name);
        validateArgs(name);
        bankService.createAccount(name);
        sessionService.login(name);
    }

    @Override
    public void deposit(BigDecimal amount) {
        checkLoggedIn(sessionService);
        validateAmount(amount);
        bankService.deposit(amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        checkLoggedIn(sessionService);
        validateAmount(amount);
        bankService.withdraw(amount);
    }

    @Override
    public void transfer(String target, BigDecimal amount) {
        checkLoggedIn(sessionService);
        validateArgs(target);
        validateAmount(amount);
        bankService.transfer(target, amount);
    }

    @Override
    public void logout() {
        checkLoggedIn(sessionService);
        String currentUser = sessionService.getCurrentUser();
        System.out.println("Goodbye, " + currentUser + "!\n");
        sessionService.logout();
    }
}