package command;

import enums.TransferMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.BankService;
import session.SessionService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandServiceImplTest {

    @Mock
    private BankService bankService;

    @Mock
    private SessionService sessionService;

    private CommandServiceImpl commandService;
    private ByteArrayOutputStream outputStream;

    private void mockLoggedInUser(String username) {
        when(sessionService.getCurrentUser()).thenReturn(username);
    }

    private void mockNotLoggedIn() {
        when(sessionService.getCurrentUser()).thenReturn(null);
    }

    @BeforeEach
    void setUp() {
        commandService = new CommandServiceImpl(bankService, sessionService);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        mockNotLoggedIn(); // Default to not logged in
    }

    // Login Tests
    @Test
    void login_Success() {
        // Arrange
        String username = "Alice";
        mockNotLoggedIn();

        // Act
        commandService.login(username);

        // Assert
        verify(bankService).createAccount(username);
        verify(sessionService).login(username);
    }

    @Test
    void login_WhenAlreadyLoggedIn_ThrowsException() {
        // Arrange
        mockLoggedInUser("Alice");

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> commandService.login("Bob")
        );
        assertTrue(exception.getMessage().contains("need to logout first"));
        verify(bankService, never()).createAccount(any());
        verify(sessionService, never()).login(any());
    }

    @Test
    void login_WithInvalidName_ThrowsException() {
        // Arrange
        mockNotLoggedIn();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.login("A1")
        );
        assertTrue(exception.getMessage().contains("Account name must be"));
        verify(bankService, never()).createAccount(any());
        verify(sessionService, never()).login(any());
    }

    // Deposit Tests
    @Test
    void deposit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100");
        mockLoggedInUser("Alice");

        // Act
        commandService.deposit(amount);

        // Assert
        verify(bankService).deposit(amount);
    }

    @Test
    void deposit_NotLoggedIn_ThrowsException() {
        // Arrange
        mockNotLoggedIn();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> commandService.deposit(new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("No user logged in"));
        verify(bankService, never()).deposit(any());
    }

    @Test
    void deposit_NegativeAmount_ThrowsException() {
        // Arrange
        mockLoggedInUser("Alice");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.deposit(new BigDecimal("-100"))
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"),
                "Expected error message not found in output: " + exception.getMessage());
        verify(bankService, never()).deposit(any());
    }

    // Withdraw Tests
    @Test
    void withdraw_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100");
        mockLoggedInUser("Alice");

        // Act
        commandService.withdraw(amount);

        // Assert
        verify(bankService).withdraw(amount);
    }

    @Test
    void withdraw_NotLoggedIn_ThrowsException() {
        // Arrange
        mockNotLoggedIn();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> commandService.withdraw(new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("No user logged in"));
        verify(bankService, never()).withdraw(any());
    }

    @Test
    void withdraw_NegativeAmount_ThrowsException() {
        // Arrange
        mockLoggedInUser("Alice");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.withdraw(new BigDecimal("-100"))
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"),
                "Expected error message not found in output: " + exception.getMessage());
        verify(bankService, never()).withdraw(any());
    }

    // Transfer Tests
    @Test
    void transfer_Success() {
        // Arrange
        String target = "Bob";
        BigDecimal amount = new BigDecimal("100");
        mockLoggedInUser("Alice");

        // Act
        commandService.transfer(target, amount);

        // Assert
        verify(bankService).setTransferMode(TransferMode.PARTIAL_ALLOWED);
        verify(bankService).transfer(target, amount);
    }

    @Test
    void transfer_NotLoggedIn_ThrowsException() {
        // Arrange
        mockNotLoggedIn();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> commandService.transfer("Bob", new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("No user logged in"));
        verify(bankService, never()).transfer(anyString(), any());
    }

    @Test
    void transfer_InvalidTargetName_ThrowsException() {
        // Arrange
        mockLoggedInUser("Alice");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.transfer("B1", new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("Account name must be"));
        verify(bankService, never()).transfer(anyString(), any());
    }

    @Test
    void transfer_NegativeAmount_ThrowsException() {
        // Arrange
        mockLoggedInUser("Alice");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.transfer("Bob", new BigDecimal("-100"))
        );
        assertTrue(exception.getMessage().contains("Amount must be positive"),
                "Expected error message not found in output: " + exception.getMessage());
        verify(bankService, never()).transfer(anyString(), any());
    }

    // Logout Tests
    @Test
    void logout_Success() {
        // Arrange
        String currentUser = "Alice";
        mockLoggedInUser(currentUser);

        // Act
        commandService.logout();

        // Assert
        verify(sessionService).logout();
        assertTrue(outputStream.toString().contains("Goodbye, Alice!"));
    }

    @Test
    void logout_NotLoggedIn_ThrowsException() {
        // Arrange
        mockNotLoggedIn();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> commandService.logout()
        );
        assertTrue(exception.getMessage().contains("No user logged in"));
        verify(sessionService, never()).logout();
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }
}