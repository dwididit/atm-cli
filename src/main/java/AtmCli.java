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

    private final CommandService commandService;
    private final SessionService sessionService;
    private final Scanner scanner;

    public AtmCli(CommandService commandService, SessionService sessionService, Scanner scanner) {
        this.commandService = commandService;
        this.sessionService = sessionService;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        AccountService accountService = new AccountServiceImpl();
        SessionService sessionService = new SessionServiceImpl();
        BankService bankService = new BankServiceImpl(accountService, sessionService);
        CommandService commandService = new CommandServiceImpl(bankService, sessionService);
        Scanner scanner = new Scanner(System.in);

        AtmCli atmCli = new AtmCli(commandService, sessionService, scanner);
        atmCli.start();
    }

    public void start() {
        printWelcomeMessage();

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) break;

            try {
                processCommand(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void printWelcomeMessage() {
        System.out.println("Welcome to ATM CLI");
        System.out.println("* `login [name]` - Logs in as this customer and creates the customer if not exist");
        System.out.println("* `deposit [amount]` - Deposits this amount to the logged in customer");
        System.out.println("* `withdraw [amount]` - Withdraws this amount from the logged in customer");
        System.out.println("* `transfer [target] [amount]` - Transfers this amount from the logged in customer to the target customer");
        System.out.println("* `logout` - Logs out of the current customer");
        System.out.println("Please type command!\n");
    }

    private void processCommand(String input) {
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
                    try {
                        BigDecimal amount = new BigDecimal(parts[1]);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Error: Amount must be positive\n");
                        } else {
                            commandService.deposit(amount);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid amount format\n");
                    }
                }
                break;
            case "withdraw":
                if (parts.length < 2) {
                    throw new IllegalArgumentException("Error: Withdraw requires amount (usage: withdraw <amount>)\n");
                } else {
                    try {
                        BigDecimal amount = new BigDecimal(parts[1]);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Error: Amount must be positive\n");
                            return;
                        }
                        commandService.withdraw(amount);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid amount format\n");
                    }
                }
                break;
            case "transfer":
                if (parts.length < 3) {
                    throw new IllegalArgumentException("Error: Transfer requires recipient account and amount (usage: transfer <account> <amount>)\n");
                } else {
                    String targetAccount = parts[1].toLowerCase();
                    String currentUser = sessionService.getCurrentUser();

                    if (targetAccount.equals(currentUser)) {
                        System.out.println("Error: Cannot transfer money to yourself\n");
                        return;
                    }

                    try {
                        BigDecimal amount = new BigDecimal(parts[2]);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Error: Amount must be positive\n");
                            return;
                        }
                        commandService.transfer(parts[1], amount);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid amount format\n");
                    }
                }
                break;
            case "logout":
                commandService.logout();
                break;
            default:
                System.out.println("Invalid command\n");
        }
    }
}