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
                            System.out.println("Error: Transfer requires recipient account and amount (usage: transfer <account> <amount>)\n");
                        } else {
                            commandService.transfer(parts[1], new BigDecimal(parts[2]));
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