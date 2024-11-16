package account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
    }

    @Test
    void createAccount_Success() {
        // Act
        Account account = accountService.createAccount("Alice");

        // Assert
        assertNotNull(account);
        assertEquals("alice", account.getName());
        assertEquals(0, account.getBalance().intValue());
    }

    @Test
    void createAccount_CaseInsensitive() {
        // Act
        Account account1 = accountService.createAccount("Alice");
        Account account2 = accountService.createAccount("ALICE");
        Account account3 = accountService.createAccount("alice");

        // Assert
        assertSame(account1, account2);
        assertSame(account2, account3);
        assertEquals("alice", account1.getName());
    }

    @Test
    void getAccount_ExistingAccount_ReturnsAccount() {
        // Arrange
        Account createdAccount = accountService.createAccount("Bob");

        // Act
        Account retrievedAccount = accountService.getAccount("Bob");

        // Assert
        assertNotNull(retrievedAccount);
        assertSame(createdAccount, retrievedAccount);
        assertEquals("bob", retrievedAccount.getName());
    }

    @Test
    void getAccount_NonExistingAccount_CreatesNewAccount() {
        // Act
        Account account = accountService.getAccount("Charlie");

        // Assert
        assertNotNull(account);
        assertEquals("charlie", account.getName());
        assertEquals(0, account.getBalance().intValue());
    }

    @Test
    void getAccount_CaseInsensitive() {
        // Arrange
        Account originalAccount = accountService.createAccount("Dave");

        // Act
        Account upperCase = accountService.getAccount("DAVE");
        Account lowerCase = accountService.getAccount("dave");
        Account mixedCase = accountService.getAccount("DaVe");

        // Assert
        assertSame(originalAccount, upperCase);
        assertSame(originalAccount, lowerCase);
        assertSame(originalAccount, mixedCase);
    }

    @Test
    void updateAccount_Success() {
        // Arrange
        Account account = accountService.createAccount("Eve");
        account.deposit(new java.math.BigDecimal("100"));

        // Act
        accountService.updateAccount(account);
        Account retrievedAccount = accountService.getAccount("Eve");

        // Assert
        assertEquals(100, retrievedAccount.getBalance().intValue());
        assertSame(account, retrievedAccount);
    }

    @Test
    void updateAccount_CaseInsensitive() {
        // Arrange
        Account originalAccount = accountService.createAccount("Frank");
        originalAccount.deposit(new java.math.BigDecimal("100"));

        // Create a new account with different case
        Account updatedAccount = new Account("FRANK");
        updatedAccount.deposit(new java.math.BigDecimal("200"));

        // Act
        accountService.updateAccount(updatedAccount);
        Account retrievedAccount = accountService.getAccount("frank");

        // Assert
        assertEquals(200, retrievedAccount.getBalance().intValue());
        assertEquals("frank", retrievedAccount.getName());
    }

    @Test
    void multipleAccounts_Independence() {
        // Arrange & Act
        Account account1 = accountService.createAccount("Grace");
        Account account2 = accountService.createAccount("Henry");

        account1.deposit(new java.math.BigDecimal("100"));
        account2.deposit(new java.math.BigDecimal("200"));

        accountService.updateAccount(account1);
        accountService.updateAccount(account2);

        // Assert
        Account retrieved1 = accountService.getAccount("Grace");
        Account retrieved2 = accountService.getAccount("Henry");

        assertEquals(100, retrieved1.getBalance().intValue());
        assertEquals(200, retrieved2.getBalance().intValue());
        assertNotSame(retrieved1, retrieved2);
    }

    @Test
    void validateAccountName_InvalidName_ThrowsException() {
        // Test various invalid names
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("A")); // Too short
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("A1")); // Contains number
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("User@Name")); // Special character
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("")); // Empty string
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("ThisNameIsTooLongForTheSystem")); // Too long
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(null)); // Null
    }
}
