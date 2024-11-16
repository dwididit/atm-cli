package service;

import account.Account;
import account.AccountService;
import enums.TransferMode;
import lombok.RequiredArgsConstructor;
import session.SessionService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final AccountService accountService;
    private final SessionService sessionService;
    private final Map<String, Map<String, BigDecimal>> owedAmounts = new HashMap<>();

    // Set transfer mode here
    private TransferMode transferMode = TransferMode.PARTIAL_ALLOWED;

    @Override
    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

    @Override
    public void createAccount(String name) {

        if (!name.matches("^[a-zA-Z]{2,20}$")) {
            throw new IllegalArgumentException("Account name must be 2-20 letters long and contain only letters (no numbers or special characters)\n");
        }

        Account account = accountService.createAccount(name);
        System.out.println("Hello, " + name + "!");
        printBalance(account);
        printOwedAmounts(name);
        System.out.println();
    }

    @Override
    public void deposit(BigDecimal amount) {
        String currentUser = sessionService.getCurrentUser();
        Account account = accountService.getAccount(currentUser);
        account.deposit(amount);
        accountService.updateAccount(account);
        handleDebts(currentUser, amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        String currentUser = sessionService.getCurrentUser();
        Account account = accountService.getAccount(currentUser);
        account.withdraw(amount);
        accountService.updateAccount(account);
        printBalance(account);
    }

    @Override
    public void transfer(String target, BigDecimal requestedAmount) {
        String currentUser = sessionService.getCurrentUser();
        target = target.toLowerCase();

        if (target.equals(currentUser)) {
            throw new IllegalStateException("Cannot transfer money to yourself");
        }

        Account sourceAccount = accountService.getAccount(currentUser);
        BigDecimal balance = sourceAccount.getBalance();

        // Handle Full Only Transfer Mode
        if (transferMode == TransferMode.FULL_ONLY) {
            if (balance.compareTo(requestedAmount) < 0) {
                System.out.println("Insufficient funds for full transfer. Required: $" + requestedAmount + ", Available: $" + balance);
                System.out.println();
                return;
            }

            Account targetAccount = accountService.getAccount(target);
            if (targetAccount == null) {
                targetAccount = accountService.createAccount(target);
            }

            sourceAccount.transfer(targetAccount, requestedAmount);
            System.out.println("Transferred $" + requestedAmount + " to " + target);
            accountService.updateAccount(sourceAccount);
            accountService.updateAccount(targetAccount);
            printBalance(sourceAccount);
            System.out.println();
            return;
        }

        // Handle Partial Allowed Transfer Mode
        if (sourceAccount.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Insufficient funds. Your balance is $" + balance);
            System.out.println();
            return;
        }

        Account targetAccount;
        try {
            targetAccount = accountService.getAccount(target);
        } catch (Exception e) {
            targetAccount = accountService.createAccount(target);
            if (sourceAccount.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                addOwedAmount(currentUser, target, requestedAmount);
                printBalance(sourceAccount);
                printOwedAmounts(currentUser);
                System.out.println();
                return;
            }
        }

        BigDecimal availableAmount = sourceAccount.getBalance();
        BigDecimal transferAmount = requestedAmount.min(availableAmount);

        if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
            sourceAccount.transfer(targetAccount, transferAmount);
            System.out.println("Transferred $" + transferAmount + " to " + target);

            if (transferAmount.compareTo(requestedAmount) < 0) {
                BigDecimal remainingAmount = requestedAmount.subtract(transferAmount);
                addOwedAmount(currentUser, target, remainingAmount);
            }
        }

        accountService.updateAccount(sourceAccount);
        accountService.updateAccount(targetAccount);
        printBalance(sourceAccount);
        printOwedAmounts(currentUser);
        System.out.println();
    }

    private void handleDebts(String currentUser, BigDecimal availableAmount) {
        Map<String, BigDecimal> debts = owedAmounts.getOrDefault(currentUser.toLowerCase(), new HashMap<>());
        BigDecimal remaining = availableAmount;

        for (Map.Entry<String, BigDecimal> debt : new HashMap<>(debts).entrySet()) {
            String creditor = debt.getKey().toLowerCase();
            BigDecimal owedAmount = debt.getValue();
            BigDecimal transferAmount = owedAmount.min(remaining);

            if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                Account targetAccount = accountService.getAccount(creditor);
                Account sourceAccount = accountService.getAccount(currentUser);
                sourceAccount.transfer(targetAccount, transferAmount);
                System.out.println("Transferred $" + transferAmount + " to " + creditor);
                accountService.updateAccount(sourceAccount);
                accountService.updateAccount(targetAccount);

                remaining = remaining.subtract(transferAmount);
                BigDecimal newOwedAmount = owedAmount.subtract(transferAmount);
                if (newOwedAmount.compareTo(BigDecimal.ZERO) > 0) {
                    debts.put(creditor, newOwedAmount);
                } else {
                    debts.remove(creditor);
                }
            }
        }

        if (debts.isEmpty()) {
            owedAmounts.remove(currentUser.toLowerCase());
        } else {
            owedAmounts.put(currentUser.toLowerCase(), debts);
        }

        Account updatedAccount = accountService.getAccount(currentUser);
        printBalance(updatedAccount);
        printOwedAmounts(currentUser);
        System.out.println();
    }

    private void addOwedAmount(String debtor, String creditor, BigDecimal amount) {
        debtor = debtor.toLowerCase();
        creditor = creditor.toLowerCase();
        owedAmounts.computeIfAbsent(debtor, k -> new HashMap<>())
                .merge(creditor, amount, BigDecimal::add);
    }


    private void printBalance(Account account) {
        System.out.println("Your balance is $" + account.getBalance());
    }

    private void printOwedAmounts(String user) {
        Map<String, BigDecimal> debts = owedAmounts.getOrDefault(user.toLowerCase(), new HashMap<>());
        for (Map.Entry<String, BigDecimal> debt : debts.entrySet()) {
            if (debt.getValue().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Owed $" + debt.getValue() + " to " + debt.getKey());
            }
        }
    }
}