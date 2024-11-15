package service;

import account.Account;
import account.AccountService;
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

    @Override
    public void createAccount(String name) {
        Account account = accountService.createAccount(name);
        System.out.println("Hello, " + name + "!");
        printBalance(account);
        System.out.println();
    }

    @Override
    public void deposit(BigDecimal amount) {
        String currentUser = sessionService.getCurrentUser();
        Account account = accountService.getAccount(currentUser);
        account.deposit(amount);
        accountService.updateAccount(account);
        printBalance(account);
        handleDebts(currentUser, amount);
        System.out.println();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        String currentUser = sessionService.getCurrentUser();
        Account account = accountService.getAccount(currentUser);
        account.withdraw(amount);
        accountService.updateAccount(account);
        printBalance(account);
        System.out.println();
    }

    @Override
    public void transfer(String target, BigDecimal requestedAmount) {
        String currentUser = sessionService.getCurrentUser();
        Account sourceAccount = accountService.getAccount(currentUser);

        // Check if user already owes money and has zero balance
        if (hasOutstandingDebt(currentUser) && sourceAccount.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("Insufficient balance. Please clear existing debts first.\n");
        }

        Account targetAccount;
        try {
            targetAccount = accountService.getAccount(target);
        } catch (Exception e) {
            targetAccount = accountService.createAccount(target);
            if (sourceAccount.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                addOwedAmount(currentUser, target, requestedAmount);
                System.out.println("Your balance is $" + sourceAccount.getBalance());
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
        System.out.println("Your balance is $" + sourceAccount.getBalance());
        printOwedAmounts(currentUser);
        System.out.println();
    }

    private boolean hasOutstandingDebt(String user) {
        Map<String, BigDecimal> debts = owedAmounts.getOrDefault(user, new HashMap<>());
        return debts.values().stream()
                .anyMatch(amount -> amount.compareTo(BigDecimal.ZERO) > 0);
    }

    @Override
    public void logout() {
        sessionService.logout();
    }

    private void handleDebts(String currentUser, BigDecimal availableAmount) {
        Map<String, BigDecimal> debts = owedAmounts.getOrDefault(currentUser, new HashMap<>());
        BigDecimal remaining = availableAmount;

        for (Map.Entry<String, BigDecimal> debt : new HashMap<>(debts).entrySet()) {
            String creditor = debt.getKey();
            BigDecimal owedAmount = debt.getValue();
            BigDecimal transferAmount = owedAmount.min(remaining);

            if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Transferred $" + transferAmount + " to " + creditor);
                Account targetAccount = accountService.getAccount(creditor);
                Account sourceAccount = accountService.getAccount(currentUser);
                sourceAccount.transfer(targetAccount, transferAmount);
                accountService.updateAccount(sourceAccount);
                accountService.updateAccount(targetAccount);

                remaining = remaining.subtract(transferAmount);
                debts.put(creditor, owedAmount.subtract(transferAmount));
            }
        }
        owedAmounts.put(currentUser, debts);
    }

    private void addOwedAmount(String debtor, String creditor, BigDecimal amount) {
        owedAmounts.computeIfAbsent(debtor, k -> new HashMap<>())
                .merge(creditor, amount, BigDecimal::add);
    }

    private void printBalance(Account account) {
        System.out.println("Your balance is $" + account.getBalance());
    }

    private void printOwedAmounts(String user) {
        Map<String, BigDecimal> debts = owedAmounts.getOrDefault(user, new HashMap<>());
        for (Map.Entry<String, BigDecimal> debt : debts.entrySet()) {
            if (debt.getValue().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Owed $" + debt.getValue() + " to " + debt.getKey());
            }
        }
    }
}