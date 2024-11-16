package account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BaseAccountTest {

    private Account account;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        account = new Account("Alice");
        targetAccount = new Account("Bob");
    }

    @Test
    void deposit_Success() {
        // Act
        account.deposit(new BigDecimal("100"));

        // Assert
        assertEquals(new BigDecimal("100"), account.getBalance());
    }

    @Test
    void deposit_MultipleDeposits_Success() {
        // Act
        account.deposit(new BigDecimal("100"));
        account.deposit(new BigDecimal("50"));
        account.deposit(new BigDecimal("25.50"));

        // Assert
        assertEquals(new BigDecimal("175.50"), account.getBalance());
    }

    @Test
    void withdraw_Success() {
        // Arrange
        account.deposit(new BigDecimal("100"));

        // Act
        account.withdraw(new BigDecimal("60"));

        // Assert
        assertEquals(new BigDecimal("40"), account.getBalance());
    }

    @Test
    void withdraw_InsufficientFunds_ThrowsException() {
        // Arrange
        account.deposit(new BigDecimal("50"));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.withdraw(new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertEquals(new BigDecimal("50"), account.getBalance()); // Balance should remain unchanged
    }

    @Test
    void withdraw_ExactBalance_Success() {
        // Arrange
        account.deposit(new BigDecimal("100"));

        // Act
        account.withdraw(new BigDecimal("100"));

        // Assert
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void transfer_Success() {
        // Arrange
        account.deposit(new BigDecimal("100"));

        // Act
        account.transfer(targetAccount, new BigDecimal("60"));

        // Assert
        assertEquals(new BigDecimal("40"), account.getBalance());
        assertEquals(new BigDecimal("60"), targetAccount.getBalance());
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        // Arrange
        account.deposit(new BigDecimal("50"));
        BigDecimal targetInitialBalance = targetAccount.getBalance();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.transfer(targetAccount, new BigDecimal("100"))
        );
        assertTrue(exception.getMessage().contains("Insufficient funds"));

        // Verify neither balance changed
        assertEquals(new BigDecimal("50"), account.getBalance());
        assertEquals(targetInitialBalance, targetAccount.getBalance());
    }

    @Test
    void transfer_EntireBalance_Success() {
        // Arrange
        account.deposit(new BigDecimal("100"));

        // Act
        account.transfer(targetAccount, new BigDecimal("100"));

        // Assert
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(new BigDecimal("100"), targetAccount.getBalance());
    }

    @Test
    void transfer_MultipleTransfers_Success() {
        // Arrange
        account.deposit(new BigDecimal("100"));

        // Act
        account.transfer(targetAccount, new BigDecimal("20"));
        account.transfer(targetAccount, new BigDecimal("30"));
        account.transfer(targetAccount, new BigDecimal("10"));

        // Assert
        assertEquals(new BigDecimal("40"), account.getBalance());
        assertEquals(new BigDecimal("60"), targetAccount.getBalance());
    }

    @Test
    void multipleMixedOperations_Success() {
        // Act & Assert
        account.deposit(new BigDecimal("100")); // Balance: 100
        assertEquals(new BigDecimal("100"), account.getBalance());

        account.withdraw(new BigDecimal("30")); // Balance: 70
        assertEquals(new BigDecimal("70"), account.getBalance());

        account.transfer(targetAccount, new BigDecimal("20")); // Balance: 50, Target: 20
        assertEquals(new BigDecimal("50"), account.getBalance());
        assertEquals(new BigDecimal("20"), targetAccount.getBalance());

        account.deposit(new BigDecimal("30")); // Balance: 80
        assertEquals(new BigDecimal("80"), account.getBalance());

        account.transfer(targetAccount, new BigDecimal("50")); // Balance: 30, Target: 70
        assertEquals(new BigDecimal("30"), account.getBalance());
        assertEquals(new BigDecimal("70"), targetAccount.getBalance());
    }
}