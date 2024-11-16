package account;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void constructor_InitializesNameAndBalance() {
        // Act
        Account account = new Account("Alice");

        // Assert
        assertEquals("alice", account.getName());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void constructor_ConvertsNameToLowerCase() {
        // Arrange & Act
        Account account1 = new Account("ALICE");
        Account account2 = new Account("Alice");
        Account account3 = new Account("aLiCe");

        // Assert
        assertEquals("alice", account1.getName());
        assertEquals("alice", account2.getName());
        assertEquals("alice", account3.getName());
    }

    @Test
    void getName_ReturnsSameName() {
        // Arrange
        Account account = new Account("Bob");

        // Act & Assert
        assertEquals("bob", account.getName());
    }

    @Test
    void getBalance_InitiallyZero() {
        // Arrange
        Account account = new Account("Charlie");

        // Act & Assert
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void equals_SameNameDifferentBalance_NotEqual() {
        // Arrange
        Account account1 = new Account("Alice");
        Account account2 = new Account("Alice");
        account1.deposit(new BigDecimal("100"));

        // Act & Assert
        assertNotEquals(account1, account2);
    }

    @Test
    void equals_DifferentNameSameBalance_NotEqual() {
        // Arrange
        Account account1 = new Account("Alice");
        Account account2 = new Account("Bob");

        // Act & Assert
        assertNotEquals(account1, account2);
    }

    @Test
    void equals_SameNameSameBalance_Equal() {
        // Arrange
        Account account1 = new Account("Alice");
        Account account2 = new Account("Alice");

        // Act & Assert
        assertEquals(account1.getName(), account2.getName());
        assertEquals(account1.getBalance(), account2.getBalance());
    }

    @Test
    void inheritedMethods_WorkCorrectly() {
        // Arrange
        Account account = new Account("Alice");
        Account targetAccount = new Account("Bob");

        // Act & Assert
        // Test deposit
        account.deposit(new BigDecimal("100"));
        assertEquals(new BigDecimal("100"), account.getBalance());

        // Test withdraw
        account.withdraw(new BigDecimal("30"));
        assertEquals(new BigDecimal("70"), account.getBalance());

        // Test transfer
        account.transfer(targetAccount, new BigDecimal("20"));
        assertEquals(new BigDecimal("50"), account.getBalance());
        assertEquals(new BigDecimal("20"), targetAccount.getBalance());
    }
}