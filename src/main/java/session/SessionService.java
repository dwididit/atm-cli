package session;

public interface SessionService {
    void login(String username);
    void logout();
    String getCurrentUser();
    boolean isLoggedIn();
}
