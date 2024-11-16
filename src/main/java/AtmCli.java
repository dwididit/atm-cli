import account.AccountService;
import account.AccountServiceImpl;
import command.CommandService;
import command.CommandServiceImpl;
import service.BankService;
import service.BankServiceImpl;
import session.SessionService;
import session.SessionServiceImpl;

import java.math.BigDecimal;
import java.util.Scanner;

public class AtmCli {
    public static void main(String[] args) {
        AccountService accountService = new AccountServiceImpl();
        SessionService sessionService = new SessionServiceImpl();
        BankService bankService = new BankServiceImpl(accountService, sessionService);
        CommandService commandService = new CommandServiceImpl(bankService, sessionService);

        System.out.println("Welcome to ATM CLI");
        System.out.println("* `login [name]` - Logs in as this customer and creates the customer if not exist");
        System.out.println("* `deposit [amount]` - Deposits this amount to the logged in customer");
        System.out.println("* `withdraw [amount]` - Withdraws this amount from the logged in customer");
        System.out.println("* `transfer [target] [amount]` - Transfers this amount from the logged in customer to the target customer");
        System.out.println("* `logout` - Logs out of the current customer");
        System.out.println("Please type below!");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) break;

            try {
                String[] parts = input.split(" ");
                switch (parts[0]) {
                    case "login":
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Error: login requires amount (usage: login <name>)\n");
                        }
                        commandService.login(parts[1]);
                        break;
                    case "deposit":
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Error: Deposit requires amount (usage: deposit <amount>)\n");
                        } else {
                            commandService.deposit(new BigDecimal(parts[1]));
                        }
                        break;
                    case "withdraw":
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Error: Withdraw requires amount (usage: withdraw <amount>)\n");
                        } else {
                            commandService.withdraw(new BigDecimal(parts[1]));
                        }
                        break;
                    case "transfer":
                        if (parts.length < 3) {
                            throw new IllegalArgumentException("Error: Transfer requires recipient account and amount (usage: transfer <account> <amount>)\n");
                        } else {
                            String targetAccount = parts[1].toLowerCase();
                            String currentUser = sessionService.getCurrentUser();

                            if (targetAccount.equals(currentUser)) {
                                throw new IllegalArgumentException("Error: Cannot transfer money to yourself\n");
                            } else {
                                commandService.transfer(parts[1], new BigDecimal(parts[2]));
                            }
                        }
                        break;
                    case "logout":
                        commandService.logout();
                        break;
                    default:
                        System.out.println("Invalid command\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}