package service;

import account.Account;
import account.AccountService;
import enums.TransferMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import session.SessionService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private SessionService sessionService;

    @Mock
    private Account sourceAccount;

    @Mock
    private Account targetAccount;

    private BankServiceImpl bankService;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        bankService = new BankServiceImpl(accountService, sessionService);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // Account Creation Tests
    @Test
    void createAccount_ValidName_Success() {
        // Arrange
        String name = "Alice";
        when(accountService.createAccount(name)).thenReturn(sourceAccount);
        when(sourceAccount.getBalance()).thenReturn(BigDecimal.ZERO);

        // Act
        bankService.createAccount(name);

        // Assert
        verify(accountService).createAccount(name);
        assertTrue(outputStream.toString().contains("Hello, Alice!"));
    }

    @Test
    void createAccount_InvalidName_ThrowsException() {
        // Arrange
        String invalidName = "Alice123";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.createAccount(invalidName)
        );
        assertTrue(exception.getMessage().contains("Account name must be 2-20 letters long"));
        verify(accountService, never()).createAccount(any());
    }

    // Deposit Tests
    @Test
    void deposit_Success() {
        // Arrange
        String currentUser = "alice";
        BigDecimal amount = new BigDecimal("100");
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(sourceAccount.getBalance()).thenReturn(amount);

        // Act
        bankService.deposit(amount);

        // Assert
        verify(sourceAccount).deposit(amount);
        verify(accountService).updateAccount(sourceAccount);
    }

    // Withdraw Tests
    @Test
    void withdraw_Success() {
        // Arrange
        String currentUser = "alice";
        BigDecimal amount = new BigDecimal("50");
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(sourceAccount.getBalance()).thenReturn(new BigDecimal("50"));

        // Act
        bankService.withdraw(amount);

        // Assert
        verify(sourceAccount).withdraw(amount);
        verify(accountService).updateAccount(sourceAccount);
    }

    // Transfer Tests
    @Test
    void transfer_FullMode_Success() {
        // Arrange
        String currentUser = "alice";
        String targetUser = "bob";
        BigDecimal amount = new BigDecimal("100");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(accountService.getAccount(targetUser)).thenReturn(targetAccount);
        when(sourceAccount.getBalance()).thenReturn(new BigDecimal("200"));

        bankService.setTransferMode(TransferMode.FULL_ONLY);

        // Act
        bankService.transfer(targetUser, amount);

        // Assert
        verify(sourceAccount).transfer(targetAccount, amount);
        verify(accountService).updateAccount(sourceAccount);
        verify(accountService).updateAccount(targetAccount);
    }

    @Test
    void transfer_FullMode_InsufficientFunds() {
        // Arrange
        String currentUser = "alice";
        String targetUser = "bob";
        BigDecimal amount = new BigDecimal("100");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(sourceAccount.getBalance()).thenReturn(new BigDecimal("50"));

        bankService.setTransferMode(TransferMode.FULL_ONLY);

        // Act
        bankService.transfer(targetUser, amount);

        // Assert
        verify(sourceAccount, never()).transfer(any(), any());
        assertTrue(outputStream.toString().contains("Insufficient funds for full transfer"));
    }

    @Test
    void transfer_PartialMode_PartialTransfer() {
        // Arrange
        String currentUser = "alice";
        String targetUser = "bob";
        BigDecimal requestedAmount = new BigDecimal("100");
        BigDecimal availableAmount = new BigDecimal("60");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(accountService.getAccount(targetUser)).thenReturn(targetAccount);
        when(sourceAccount.getBalance()).thenReturn(availableAmount);

        bankService.setTransferMode(TransferMode.PARTIAL_ALLOWED);

        // Act
        bankService.transfer(targetUser, requestedAmount);

        // Assert
        verify(sourceAccount).transfer(targetAccount, availableAmount);
        assertTrue(outputStream.toString().contains("Transferred $60"));
    }

    @Test
    void transfer_ToSelf_ThrowsException() {
        // Arrange
        String currentUser = "alice";
        when(sessionService.getCurrentUser()).thenReturn(currentUser);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bankService.transfer(currentUser, new BigDecimal("100"))
        );
        assertEquals("Cannot transfer money to yourself", exception.getMessage());
    }

    // Debt Handling Tests
    @Test
    void deposit_WithExistingDebt_HandlesDebtRepayment() {
        // Arrange
        String currentUser = "alice";
        String creditor = "bob";
        BigDecimal depositAmount = new BigDecimal("100");
        BigDecimal debtAmount = new BigDecimal("50");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(accountService.getAccount(currentUser)).thenReturn(sourceAccount);
        when(accountService.getAccount(creditor)).thenReturn(targetAccount);
        when(sourceAccount.getBalance()).thenReturn(depositAmount);

        // Create initial debt
        bankService.setTransferMode(TransferMode.PARTIAL_ALLOWED);
        bankService.transfer(creditor, debtAmount);

        // Act
        bankService.deposit(depositAmount);

        // Assert
        verify(sourceAccount).transfer(targetAccount, debtAmount);
        assertTrue(outputStream.toString().contains("Transferred $50 to bob"));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }
}
