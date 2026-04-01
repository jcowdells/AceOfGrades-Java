package aog;

import auth.AogRole;

public class User {
    private final int id;
    private final String username;
    private final String email_address;
    private final AogRole role;

    public User(int id, String username, String email_address, AogRole role) {
        this.id = id;
        this.username = username;
        this.email_address = email_address;
        this.role = role;
    }

    public static User anyone() {
        return new User(-1, "", "", AogRole.ANYONE);
    }

    public int getID() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmailAddress() {
        return email_address;
    }

    public AogRole getRole() {
        return role;
    }

}
