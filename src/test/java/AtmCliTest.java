import command.CommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import session.SessionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtmCliTest {

    @Mock
    private CommandService commandService;

    @Mock
    private SessionService sessionService;

    private ByteArrayOutputStream outputStream;
    private Scanner scanner;
    private AtmCli atmCli;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private void setupAtmWithInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        scanner = new Scanner(System.in);
        atmCli = new AtmCli(commandService, sessionService, scanner);
    }

    @Test
    void processCommand_Login_Success() {
        // Arrange
        setupAtmWithInput("login alice\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService).login("alice");
    }

    @Test
    void processCommand_LoginWithoutName_ThrowsError() {
        // Arrange
        setupAtmWithInput("login\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).login(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: login requires amount (usage: login <name>)"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_Deposit_Success() {
        // Arrange
        setupAtmWithInput("deposit 100\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService).deposit(new BigDecimal("100"));
    }

    @Test
    void processCommand_DepositWithoutAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("deposit\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).deposit(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Deposit requires amount"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_Transfer_Success() {
        // Arrange
        setupAtmWithInput("transfer bob 50\nexit\n");
        when(sessionService.getCurrentUser()).thenReturn("alice");

        // Act
        atmCli.start();

        // Assert
        verify(commandService).transfer("bob", new BigDecimal("50"));
    }

    @Test
    void processCommand_TransferToSelf_ThrowsError() {
        // Arrange
        setupAtmWithInput("transfer alice 50\nexit\n");
        when(sessionService.getCurrentUser()).thenReturn("alice");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).transfer(anyString(), any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Cannot transfer money to yourself"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_WithdrawWithoutAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("withdraw\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).withdraw(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Withdraw requires amount"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_InvalidCommand_PrintsError() {
        // Arrange
        setupAtmWithInput("invalid\nexit\n");

        // Act
        atmCli.start();

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid command"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_LogoutSuccess() {
        // Arrange
        setupAtmWithInput("logout\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService).logout();
    }

    @Test
    void processCommand_LoginWithEmptyUsername_ThrowsError() {
        // Arrange
        setupAtmWithInput("login \nexit\n");  // Note the space after login

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).login(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: login requires amount"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_DepositNegativeAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("deposit -100\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).deposit(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Amount must be positive"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_DepositInvalidAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("deposit abc\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).deposit(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid amount format"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_Withdraw_Success() {
        // Arrange
        setupAtmWithInput("withdraw 100\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService).withdraw(new BigDecimal("100"));
    }

    @Test
    void processCommand_WithdrawNegativeAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("withdraw -50\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).withdraw(any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Amount must be positive"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_TransferWithoutTargetAccount_ThrowsError() {
        // Arrange
        setupAtmWithInput("transfer 100\nexit\n");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).transfer(any(), any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Transfer requires recipient account and amount"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_TransferNegativeAmount_ThrowsError() {
        // Arrange
        setupAtmWithInput("transfer bob -50\nexit\n");
        when(sessionService.getCurrentUser()).thenReturn("alice");

        // Act
        atmCli.start();

        // Assert
        verify(commandService, never()).transfer(any(), any());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Amount must be positive"),
                "Expected error message not found in output: " + output);
    }

    @Test
    void processCommand_LogoutWhenNotLoggedIn_ThrowsError() {
        // Arrange
        setupAtmWithInput("logout\nexit\n");
        doThrow(new IllegalStateException("Not logged in"))
                .when(commandService).logout();

        // Act
        atmCli.start();

        // Assert
        verify(commandService).logout();
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Not logged in"),
                "Expected error message not found in output: " + output);
    }

    @AfterEach
    void tearDown() {
        System.setIn(System.in);
        System.setOut(System.out);
        if (scanner != null) {
            scanner.close();
        }
    }
}