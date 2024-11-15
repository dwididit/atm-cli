package session;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private String currentUser;

    @Override
    public void login(String username) {
        this.currentUser = username;
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
