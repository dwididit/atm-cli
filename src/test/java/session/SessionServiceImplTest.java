package session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceImplTest {

    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl();
    }

    @Test
    void initialState_NoUserLoggedIn() {
        // Assert
        assertNull(sessionService.getCurrentUser());
        assertFalse(sessionService.isLoggedIn());
    }

    @Test
    void login_Success() {
        // Act
        sessionService.login("Alice");

        // Assert
        assertEquals("alice", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void login_CaseInsensitive() {
        // Act
        sessionService.login("ALICE");

        // Assert
        assertEquals("alice", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void login_MultipleUsers_OnlyLastUserActive() {
        // Act
        sessionService.login("Alice");
        sessionService.login("Bob");

        // Assert
        assertEquals("bob", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void login_SameUserMultipleTimes_RemainsLoggedIn() {
        // Act
        sessionService.login("Alice");
        sessionService.login("ALICE");
        sessionService.login("alice");

        // Assert
        assertEquals("alice", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void login_WithWhitespace_TrimsAndConvertsToLowerCase() {
        // Act
        sessionService.login(" Alice ");

        // Assert
        assertEquals("alice", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void logout_WhenLoggedIn_Success() {
        // Arrange
        sessionService.login("Alice");

        // Act
        sessionService.logout();

        // Assert
        assertNull(sessionService.getCurrentUser());
        assertFalse(sessionService.isLoggedIn());
    }

    @Test
    void logout_WhenNotLoggedIn_Success() {
        // Act
        sessionService.logout();

        // Assert
        assertNull(sessionService.getCurrentUser());
        assertFalse(sessionService.isLoggedIn());
    }

    @Test
    void logout_MultipleLogouts_Success() {
        // Arrange
        sessionService.login("Alice");

        // Act
        sessionService.logout();
        sessionService.logout();

        // Assert
        assertNull(sessionService.getCurrentUser());
        assertFalse(sessionService.isLoggedIn());
    }

    @Test
    void isLoggedIn_Consistency() {
        // Initially not logged in
        assertFalse(sessionService.isLoggedIn());
        assertNull(sessionService.getCurrentUser());

        // After login
        sessionService.login("Alice");
        assertTrue(sessionService.isLoggedIn());
        assertNotNull(sessionService.getCurrentUser());

        // After logout
        sessionService.logout();
        assertFalse(sessionService.isLoggedIn());
        assertNull(sessionService.getCurrentUser());
    }

    @Test
    void getCurrentUser_ReturnsCorrectUser() {
        // Initial state
        assertNull(sessionService.getCurrentUser());

        // After login
        sessionService.login("Alice");
        assertEquals("alice", sessionService.getCurrentUser());

        // After login with different user
        sessionService.login("Bob");
        assertEquals("bob", sessionService.getCurrentUser());

        // After logout
        sessionService.logout();
        assertNull(sessionService.getCurrentUser());
    }

    @Test
    void loginLogoutLogin_Success() {
        // First login
        sessionService.login("Alice");
        assertEquals("alice", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());

        // Logout
        sessionService.logout();
        assertNull(sessionService.getCurrentUser());
        assertFalse(sessionService.isLoggedIn());

        // Second login
        sessionService.login("Bob");
        assertEquals("bob", sessionService.getCurrentUser());
        assertTrue(sessionService.isLoggedIn());
    }
}
