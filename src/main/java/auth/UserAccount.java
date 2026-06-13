package auth;

import enums.AccessRole;

public class UserAccount {

    protected String username;
    protected String passwordHash;
    protected AccessRole role;

    public UserAccount(String username, String passwordHash, AccessRole role){
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    public String getUsername() {
        return this.username;
    }

    public boolean login(String enteredUsername, String enteredPassword) {
        return this.username.equals(enteredUsername) && this.passwordHash.equals(enteredPassword);
    }
    public void logout() {
        System.out.println("Logged out Successfully.");
    }

    /** Returns the stored password (plain text as used in this system). */
    public String getRawPassword() {
        return this.passwordHash;
    }
}
