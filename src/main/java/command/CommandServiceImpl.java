package command;

import enums.TransferMode;
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

        String currentUser = sessionService.getCurrentUser();
        if (currentUser != null && !currentUser.isEmpty()) {
            throw new IllegalStateException(currentUser + " need to logout first\n");
        }

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
        bankService.setTransferMode(TransferMode.PARTIAL_ALLOWED);
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