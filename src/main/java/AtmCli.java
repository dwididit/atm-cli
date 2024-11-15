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
                        commandService.login(parts[1]);
                        break;
                    case "deposit":
                        commandService.deposit(new BigDecimal(parts[1]));
                        break;
                    case "withdraw":
                        commandService.withdraw(new BigDecimal(parts[1]));
                        break;
                    case "transfer":
                        commandService.transfer(parts[1], new BigDecimal(parts[2]));
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