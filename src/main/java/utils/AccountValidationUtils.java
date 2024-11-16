package utils;

public class AccountValidationUtils {

    private static final String NAME_PATTERN = "^[a-zA-Z]{2,20}$";

    public static void validateAccountName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Account name must be 2-20 letters long and contain only letters (no numbers or special characters)\n");
        }

        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Account name must be 2-20 letters long and contain only letters (no numbers or special characters)\n");
        }
    }
}
